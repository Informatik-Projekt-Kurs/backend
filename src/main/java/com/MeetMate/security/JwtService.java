package com.MeetMate.security;

import com.MeetMate.Experiments.Experimentational;
import com.MeetMate.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    //Test key
    private static final String SECRET_KEY = "a24077e68b6fc44428cef75c23d8e9fa70c0bfe2851acbc584cae293cd674b66";

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractUserEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        long expirationDate = extractClaim(token, Claims::getExpiration).getTime();
        return expirationDate < System.currentTimeMillis();
    }

    public String generateToken(Map<String, Object> claims, @NotNull User user) {
        return Jwts
                .builder()
                .setSubject(user.getEmail())
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) // expires in 5 minutes
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Claims::getSubject
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims -> Claims.getSubject());
    }

    @Experimentational
    @SuppressWarnings("unchecked")
    public <ContentType> ContentType extractClaimGeneric(String claimName, String token) {
        Claims claims = extractAllClaims(token);
        return (ContentType) claims.get(claimName);
    }

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

    private Key getSigningKey() {
        byte[] signingKey = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(signingKey);
    }
}