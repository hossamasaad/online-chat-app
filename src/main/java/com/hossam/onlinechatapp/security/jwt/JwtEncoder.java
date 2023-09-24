package com.hossam.onlinechatapp.security.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtEncoder {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${application.security.jwt.expiration}")
    private long ACCESS_TOKE_EXPIRATION;

    @Value("${application.security.jwt.refresh.expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    @Getter private String accessToken;
    @Getter private String refreshToken;
    private UserDetails userDetails;
    private Map<String, Object> claims;

    public void encode(UserDetails userDetails) {
        this.encode(userDetails, new HashMap<>());
    }

    public void encode(UserDetails userDetails, Map<String, Object> claims) {
        this.userDetails = userDetails;
        this.claims = claims;
        this.accessToken = buildToken(ACCESS_TOKE_EXPIRATION);
        this.refreshToken = buildToken(REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(long EXPIRATION) {
         return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                    .compact();
    }

}
