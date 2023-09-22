package com.hossam.onlinechatapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hossam.onlinechatapp.config.AuthResponse;
import com.hossam.onlinechatapp.model.Token;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.TokenRepository;
import com.hossam.onlinechatapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(
            UserRepository userRepository,
            TokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("login") + "@chat-app.com";

        User user = userRepository.findUserByEmail(email);

        // Sign up with GitHub
        if (user == null) {
            // Simple logic of retrieving user data
            String[] name = ((String) Objects.requireNonNull(oauth2User.getAttribute("name"))).split(" ", 2);
            String firstName = name[0];
            String lastName = name[1];
            String password = email + "@secret";

            // Create the user
            User createdUser = User.builder()
                    .id(null)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            userRepository.save(createdUser);
        }
        // Login with GitHub
        else {
            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            revokeAllTokens(user);
            saveToken(user, accessToken);
            writeAuthResponse(response, accessToken, refreshToken);
        }

    }

    private void writeAuthResponse(
            HttpServletResponse response,
            String accessToken,
            String refreshToken
    ) throws IOException {
        try (OutputStream outputStream = response.getOutputStream()) {
            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
            new ObjectMapper().writeValue(outputStream, authResponse);
        }
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
