package com.TM.taskmanager.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service - Handles JWT token operations (generation, validation, parsing)
 * This service is responsible for creating secure tokens and validating them
 */
@Service
public class JwtService {

    // Secret key used to sign and verify JWT tokens (256-bit hex string)
    private static final String SECRET_KEY = "7134743677397A24432646294A404E635266556A586E3272357538782F413F44";

    public String extractUsername(String token) {
        // Extract the 'subject' claim which contains the username
        return extractAllClaims(token, Claims::getSubject);
    }

    public <T> T extractAllClaims(String token, Function<Claims, T> claimsResolver) {
        // First get all claims from the token
        final Claims claims = extractAllClaims(token);
        // Then apply the resolver function to get specific claim
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        // Generate token with empty extra claims
        return generateToken(new HashMap<>(), userDetails);
    }

    // JwtService.generateToken
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Set custom claims (roles, permissions)
                .setSubject(userDetails.getUsername()) // Set username(email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set creation time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Set expiry (24 hours)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign with secret key
                .compact(); // Build and return the final token
    }

    // JwtService.isToken Validate()
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // Extract username from token
        final String username = extractUsername(token);
        // Check if username matches AND token is not expired
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        // Compare expiration date with current date
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        // Extract the 'expiration' claim from token
        return extractAllClaims(token, Claims::getExpiration);
    }

    // JwtService.extractAllClaim()
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // use secret key to validation
                .build()
                .parseClaimsJws(token) // Parse and validate signature
                .getBody(); // Return payload
    }

    private Key getSignInKey() {
        // Decode the BASE64 secret key string into bytes
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // Create HMAC-SHA256 key from the bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
