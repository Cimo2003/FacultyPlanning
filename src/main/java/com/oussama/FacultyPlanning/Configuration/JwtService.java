package com.oussama.FacultyPlanning.Configuration;

import com.oussama.FacultyPlanning.User.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    public String generateToken(UserDetails userDetails) {
        User user = (User) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getAuthorities());
        claims.put("user_id", user.getId());
        claims.put("full_name", user.getFullName());
        return createToken(claims, user.getUsername(), accessTokenExpiration, true);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        User user = (User) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("full_name", user.getFullName());
        return createToken(claims, user.getUsername(), refreshTokenExpiration, false);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration, Boolean type) {
        HashMap<String,Object> headers = new HashMap<>();
        headers.put("type", "jwt");
        headers.put("client", "web-app");
        if(type){ headers.put("type_token", "access_token"); } else { headers.put("type_token", "refresh_token"); }
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("GSB-Server back-end")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Map<String, Object> getHeadersFromToken(String token) {
        try {
            Jwt<JwsHeader, Claims> jwt = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);

            return jwt.getHeader();
        } catch (MalformedJwtException e) {
            // Handle invalid token exception
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }
}

