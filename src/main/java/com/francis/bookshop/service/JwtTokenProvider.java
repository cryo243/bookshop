package com.francis.bookshop.service;

import com.francis.bookshop.dto.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expiration;

  private SecretKey key;

  @PostConstruct
  public void init() {
    // Ensure your secret is at least 32 bytes for HS256
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(UserDto user) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("role", user.getRole())
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public String getRole(String token) {
    return (String) parseClaims(token).get("role");
  }

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
