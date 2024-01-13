package com.MeetMate.security;

import com.MeetMate.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;

import java.security.Key;
import java.time.LocalDate;
import java.util.Date;
import java.util.function.Function;

public class JwtService {

    //Test key
    private static final String SECRET_KEY = "a24077e68b6fc44428cef75c23d8e9fa70c0bfe2851acbc584cae293cd674b66";

    //Claims::getSubject
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims -> Claims.getSubject());
    }

    public String generateToken(MultiValueMap<String, Object> claims, User user) {
        return Jwts
                .builder()
                .setSubject(user.getEmail())
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Extract claim of Type ContentType
    //Function claimsResolver takes a Claim and returns a ContentType
    public <ContentType> ContentType extractClaim(String token, Function<Claims, ContentType> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Decode the signingKey
    private Key getSigningKey() {
        byte[] signingKey = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(signingKey);
    }
}
