package com.example.demo.security;

import io.jsonwebtoken.*;
import java.util.*;

public class JwtUtil {

    private final String secret;
    private final long validity;

    public JwtUtil(String secret, long validity) {
        this.secret = secret;
        this.validity = validity;
    }

    public String generateToken(Long userId, String email, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("email", email)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        return ((Number) getClaims(token).get("userId")).longValue();
    }

    public String getRoleFromToken(String token) {
        return (String) getClaims(token).get("role");
    }

    public String getUsernameFromToken(String token) {
        return (String) getClaims(token).get("email");
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token).getBody();
    }
}
