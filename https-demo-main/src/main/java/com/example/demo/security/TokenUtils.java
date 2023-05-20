package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

@Component
public class TokenUtils {

    @Value("spring-security-example")
    private String APP_NAME;

    @Value("$ECR3T-K3Y")
    public String SECRET;

    // 18000000 = 5h
    @Value("900000")
    private int EXPIRES_IN;



    @Value("Authorization")
    private String AUTH_HEADER;

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;


    public String generateToken(String email,String role) {
        return Jwts.builder()
                .claim("role", role)
                .setIssuer(APP_NAME)
                .setSubject(email)
                .setAudience("web")
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate())
                .signWith(SIGNATURE_ALGORITHM, SECRET).compact();
    }

    public String generateTokenFromEmail(String email) {
        return Jwts.builder().setSubject(email).setIssuedAt(new Date())
                .setExpiration(generateExpirationDate()).signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + EXPIRES_IN);
    }

    public String getToken(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        String authHeader = getAuthHeaderFromHeader(request);
        System.out.println(authHeader+"da li je ovo");
        // Bearer sklj.blab.labal
        if (authHeader != null && authHeader.startsWith("Bearer "))
            return authHeader.substring(7);
        return null;
    }
    public String getUsernameFromToken(String token) {
        String userEmail;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            userEmail = claims.getSubject();
        } catch (Exception e) {
            userEmail = null;
        }
        return userEmail;
    }

    public String getEmailDirectlyFromHeader(HttpServletRequest request) {
        return this.getEmailFromToken(this.getToken(request));
    }

    public String getEmailFromToken(String token) throws ExpiredJwtException {
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            if (claims != null)
                return claims.getSubject();
        } catch (Exception ignored) {
        }
        return null;
    }

    public void getIssuedAtDateFromToken(String token) throws ExpiredJwtException {
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            if (claims != null)
                claims.getIssuedAt();
        } catch (Exception ignored) {
        }
    }

    public Date getExpirationDateFromToken(String token) throws ExpiredJwtException {
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            if (claims != null)
                return claims.getExpiration();
        } catch (Exception ignored) {
        }
        return null;
    }

    private Claims getAllClaimsFromToken(String token) throws ExpiredJwtException {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        getIssuedAtDateFromToken(token);
        return (email != null && email.equals(userDetails.getUsername())); // getUsername zapravo dobavlja email
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

    public int getExpiredIn() {
        return EXPIRES_IN;
    }

}
