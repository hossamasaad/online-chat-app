package com.hossam.onlinechatapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hossam.onlinechatapp.config.AuthRequest;
import com.hossam.onlinechatapp.config.AuthResponse;
import com.hossam.onlinechatapp.model.Token;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.TokenRepository;
import com.hossam.onlinechatapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;


@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenRepository tokenRepository,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }


    public User register(User user) {
        User createdUser = User.builder()
                               .id(null)
                               .firstName(user.getFirstName())
                               .lastName(user.getLastName())
                               .email(user.getEmail())
                               .password(passwordEncoder.encode(user.getPassword()))
                               .createdAt(new Timestamp(System.currentTimeMillis()))
                               .build();


        return userRepository.save(createdUser);
    }


    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findUserByEmail(
                request.getEmail()
        );

        String jwtToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        revokeAllTokens(user);
        saveToken(user, jwtToken);

        return new AuthResponse(jwtToken, refreshToken);
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String refreshToken = authHeader.substring(7);
        final String email = jwtTokenProvider.extractEmail(refreshToken);

        if (email == null) {
            return;
        }

        User user = userRepository.findUserByEmail(email);
        if (user != null && jwtTokenProvider.isTokenValid(refreshToken, user)) {
            String accessToken = jwtTokenProvider.generateToken(user);
            revokeAllTokens(user);
            saveToken(user, accessToken);
            writeAuthResponse(response, accessToken, refreshToken);
            return;
        }

        throw new DataIntegrityViolationException("Token is invalid ");
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
