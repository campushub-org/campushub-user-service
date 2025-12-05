package com.campushub.user.security;



import com.campushub.user.model.User;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;

import jakarta.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;



import java.security.Key;

import java.util.Date;



@Service

public class JwtService {



    private final Key secretKey;

    private final long tokenValidityInMs = 1000 * 60 * 60 * 10; // 10 hours



    public JwtService(@Value("${jwt.secret}") String secret) {

        byte[] secretBytes = DatatypeConverter.parseHexBinary(secret);

        this.secretKey = Keys.hmacShaKeyFor(secretBytes);

    }



    public String generateToken(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + tokenValidityInMs);



        return Jwts.builder()

                .setSubject(user.getUsername())

                .setIssuedAt(now)

                .setExpiration(expiryDate)

                .signWith(secretKey)

                .compact();

    }



     public String generateTokenForUser(User user) {

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + tokenValidityInMs);



        return Jwts.builder()

                .setSubject(user.getUsername())

                                .claim("role", user.getRole().name())

                                .claim("id", user.getId()) // Added user ID

                                .setIssuedAt(now)

                                .setExpiration(expiryDate)

                                .signWith(secretKey)

                                .compact();

    }

}


