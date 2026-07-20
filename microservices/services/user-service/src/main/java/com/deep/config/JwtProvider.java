package com.deep.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtProvider {

    // This converts your plain-text secret string into a proper cryptographic SecretKey object
    // (HMAC-SHA is the signing algorithm being used).
    private final SecretKey key = Keys.hmacShaKeyFor(
            JWTConstant.SECRET_KEY.getBytes()
    );

    // Authentication is a Spring Security object representing "who is currently logged in and what can they do."
    public String generateToken(Authentication auth, Long userId) {
        // This means the collection contains objects that implement GrantedAuthority or extend it.
        // getAuthorities() returns their roles/permissions (e.g., ROLE_USER, ROLE_ADMIN).
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);
        return Jwts.builder()
                // timestamp of creation
                .issuedAt(new Date())
                // expires in 86400000 ms = 24 hours
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                // .claim("email", ...), .claim("authorities", ...), .claim("userid", ...)
                // — these are custom pieces of data embedded in the token's payload. auth.getName() typically returns the username used to log in (here, the email).
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .claim("userid", userId)
                .signWith(key)
                .compact();
    }

    public String getEmailFromJwtToken(String jwt) {
        // Strips the "Bearer " prefix if present, leaving just the raw token.
        if (jwt.startsWith(JWTConstant.TOKEN_PREFIX)) {
            jwt = jwt.substring(JWTConstant.TOKEN_PREFIX.length());
        }
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(jwt).getPayload();
        return String.valueOf(claims.get("email"));
    }

    // populateAuthorities turns this into a single comma-separated string like "ROLE_USER,ROLE_ADMIN".
    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for(GrantedAuthority authority: authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
