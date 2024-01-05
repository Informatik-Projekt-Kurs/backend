package com.MeetMate.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.function.Function;

public class JwtService {

    //Test key
    private static final String SECRET_KEY = "a24077e68b6fc44428cef75c23d8e9fa70c0bfe2851acbc584cae293cd674b66";

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
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
