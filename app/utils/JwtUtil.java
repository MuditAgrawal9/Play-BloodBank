package utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.enums.Role;

import java.util.Date;

public class JwtUtil {

  private static final String SECRET = "bloodbankmanagementsystemsecretkey123456789";

  public static String generateToken(Long userId, Role role) {

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("role", role.name())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(SignatureAlgorithm.HS256, SECRET)
        .compact();
  }

  public static Claims validateToken(String token) {

    return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
  }
}
