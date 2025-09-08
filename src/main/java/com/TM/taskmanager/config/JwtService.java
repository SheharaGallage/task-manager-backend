package com.TM.taskmanager.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service - Handles JWT token operations (generation, validation, parsing)
 * This service is responsible for creating secure tokens and validating them
 */
public class JwtService {

    // Secret key used to sign and verify JWT tokens (256-bit hex string)
    private static final String SECRET_KEY = "7134743677397A24432646294A404E635266556A586E3272357538782F413F44";

    /**
     * Extract username from JWT token
     * 
     * @param token JWT token string
     * @return username (subject) from the token
     */
    public String extractUsername(String token) {
        // Extract the 'subject' claim which contains the username
        return extractAllClaims(token, Claims::getSubject);
    }

    /**
     * Generic method to extract any claim from JWT token
     * 
     * @param token          JWT token string
     * @param claimsResolver Function to extract specific claim
     * @return The extracted claim value
     */
    public <T> T extractAllClaims(String token, Function<Claims, T> claimsResolver) {
        // First get all claims from the token
        final Claims claims = extractAllClaims(token);
        // Then apply the resolver function to get specific claim
        return claimsResolver.apply(claims);
    }

    /**
     * Generate JWT token for user (without extra claims)
     * 
     * @param userDetails User information
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        // Generate token with empty extra claims
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate JWT token with custom claims
     * 
     * @param extraClaims Additional data to include in token (roles, permissions,
     *                    etc.)
     * @param userDetails User information
     * @return JWT token string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Set custom claims (roles, permissions)
                .setSubject(userDetails.getUsername()) // Set username as subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set creation time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Set expiry (24 hours)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign with secret key
                .compact(); // Build and return the final token
    }

    /**
     * Validate if JWT token is valid for a specific user
     * 
     * @param token       JWT token string
     * @param userDetails User information to validate against
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // Extract username from token
        final String username = extractUsername(token);
        // Check if username matches AND token is not expired
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Check if JWT token has expired
     * 
     * @param token JWT token string
     * @return true if expired, false if still valid
     */
    private boolean isTokenExpired(String token) {
        // Compare expiration date with current date
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date from JWT token
     * 
     * @param token JWT token string
     * @return expiration date
     */
    private Date extractExpiration(String token) {
        // Extract the 'expiration' claim from token
        return extractAllClaims(token, Claims::getExpiration);
    }

    /**
     * Parse JWT token and extract all claims (payload data)
     * This is the core method that decodes the JWT token
     * 
     * @param token JWT token string
     * @return Claims object containing all token data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder() // Create JWT parser
                .setSigningKey(getSignInKey()) // Set secret key for verification
                .build() // Build the parser
                .parseClaimsJws(token) // Parse and validate token
                .getBody(); // Return the claims (payload)
    }

    /**
     * Create signing key from secret key
     * This method converts the secret key string into a cryptographic key
     * 
     * @return HMAC-SHA256 key for signing/verifying tokens
     */
    private Key getSignInKey() {
        // Decode the BASE64 secret key string into bytes
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // Create HMAC-SHA256 key from the bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
