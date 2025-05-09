package com.carrefour.leasing.application.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    // Utilise une clé fixe pour assurer la compatibilité entre la génération et la validation du token
    private final String secretKey = "1222XAZEZREZGRGRGRGrgrrRBRBRrgrBVREebeebrbrBrb"; // Remplace par une clé secrète stable
    private final long EXPIRATION_TIME = 86400000; // 1 jour

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)  // Utilisation de la clé secrète stable
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
