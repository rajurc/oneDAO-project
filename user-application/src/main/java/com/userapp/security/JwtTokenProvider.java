package com.userapp.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
    public class JwtTokenProvider {
        private final String SECRET_KEY = "YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough";
        private final long EXPIRATION_TIME = 3600000; // 24 hours
        private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        public String generateToken(UserDetails userDetails) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }

        public String extractUsername(String token) {
            return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        }

        public boolean validateToken(String token, UserDetails userDetails) {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }

        private boolean isTokenExpired(String token) {
            return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
        }

    }
