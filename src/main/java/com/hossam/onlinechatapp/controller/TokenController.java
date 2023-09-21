package com.hossam.onlinechatapp.controller;


import com.hossam.onlinechatapp.config.AuthResponse;
import com.hossam.onlinechatapp.model.Token;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.TokenRepository;
import com.hossam.onlinechatapp.repository.UserRepository;
import com.hossam.onlinechatapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TokenController {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public TokenController(TokenRepository tokenRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping("/api/auth/token")
    public AuthResponse home(@RequestParam("id") String id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        String jwtToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        revokeAllTokens(user);
        saveToken(user, jwtToken);

        return new AuthResponse(jwtToken, refreshToken);
    }


    private void saveToken(User user, String jwtToken) {
        Token token = Token.builder()
                .userId(user.getId())
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllTokens(User user) {

        List<Token> validTokens = tokenRepository.findAllByUserId(user.getId());

        if (validTokens.isEmpty())
            return;

        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validTokens);
    }
}
