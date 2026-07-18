package com.productivity.tracker.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

	 @Value("${jwt.secret}")
	    private String secret;

	    @Value("${jwt.expiration}")
	    private long expiration;
	    
	    private SecretKey getSignKey() {
	        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	    }
	    
	    public String generateToken(String email) {

	        return Jwts.builder()
	                .subject(email)
	                .issuedAt(new Date())
	                .expiration(new Date(System.currentTimeMillis() + expiration))
	                .signWith(getSignKey())
	                .compact();
	    }
	    public String extractEmail(String token) {
	        return extractClaims(token).getSubject();
	    }

	    private Claims extractClaims(String token) {

	        return Jwts.parser()
	                .verifyWith(getSignKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	    }

	    public boolean isTokenValid(String token, String email) {

	        String extractedEmail = extractEmail(token);

	        return extractedEmail.equals(email)
	                && !isTokenExpired(token);
	    }

	    private boolean isTokenExpired(String token) {

	        return extractClaims(token)
	                .getExpiration()
	                .before(new Date());
	    }
}