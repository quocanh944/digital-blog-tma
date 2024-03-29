package com.tma.digital_blog.security;

import com.tma.digital_blog.model.User;
import com.tma.digital_blog.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
@AllArgsConstructor
public class JWTTokenProvider {

    private final UserRepository userRepository;
    private static final long EXPIRATION_TIME = 900_000; // 15 minutes
    private static final String SECRET_KEY_ACCESS = "digitalblogtmaaccess";
    private static final String SECRET_KEY_REFRESH = "digitalblogtmarefresh";

    public String createToken(Authentication authentication){
        String username = authentication.getName();

        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY_ACCESS)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("username", username)
                .claim("role", authentication.getAuthorities())
                .compact();
    }

    public String createRefreshToken(String username){
        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username is invalid.")
        );
        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY_REFRESH)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("username", user.getUsername())
                .compact();
    }

    public String getUsernameFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY_ACCESS)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    public String getUsernameFromRefreshToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY_REFRESH)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    public boolean validateAccessToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY_ACCESS).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Access JWT was expired or incorrect");
        }
    }
    public boolean validateRefreshToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY_REFRESH).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Refresh JWT was expired or incorrect");
        }
    }
}
