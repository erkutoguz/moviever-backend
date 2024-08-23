package com.erkutoguz.moviever_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;


@Service
public class JwtService {

    @Value("${spring.security.JWT_SECRET}")
    private String JWT_SECRET;

    @Value("${spring.security.ACCESS_EXPIRATION}")
    private int ACCESS_EXPIRATION;

    @Value("${spring.security.RESET_PASSWORD_EXPIRATION}")
    private int RESET_PASSWORD_EXPIRATION;

    @Value("${spring.security.REFRESH_EXPIRATION}")
    private int REFRESH_EXPIRATION;


    public String generateAccessToken(UserDetails user){
        HashMap<String, Object> claims = new HashMap<>();
        return buildToken(claims, ACCESS_EXPIRATION, user);
    }

    public String generateResetPasswordToken(UserDetails user) {
        HashMap<String, Object> claims = new HashMap<>();
        return buildToken(claims, RESET_PASSWORD_EXPIRATION, user);
    }

    public String generateRefreshToken(UserDetails user) {
        HashMap<String, Object> claims = new HashMap<>();
        return buildToken(claims, REFRESH_EXPIRATION, user);
    }


    public boolean validateToken(String token, String username) {
        return new Date().before(extractExpiration(token)) && extractUsername(token).equals(username);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }


    private String buildToken(HashMap<String, Object> claims, int expiration, UserDetails user) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .signWith(signInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(signInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key signInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
