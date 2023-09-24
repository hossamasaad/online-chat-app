package com.hossam.onlinechatapp.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtDecoder {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    private String token;

    @Getter
    private String subject;

    @Getter
    private Date expirationDate;

    @Getter
    private Map<String, Object> claims;

    public void decode(String token){
        this.token = token;
        this.startDecoding();
    }

    private void startDecoding() {
        Claims extractedClaims = Jwts.parserBuilder()
                                     .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                                     .build()
                                     .parseClaimsJws(token)
                                     .getBody();

        this.subject = extractedClaims.getSubject();
        this.expirationDate = extractedClaims.getExpiration();
        this.claims = extractedClaims.entrySet()
                                     .stream()
                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Object getClaim(String claimName){
        return claims.get(claimName);
    }
}
