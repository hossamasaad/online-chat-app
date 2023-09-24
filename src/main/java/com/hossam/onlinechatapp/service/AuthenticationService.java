package com.hossam.onlinechatapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hossam.onlinechatapp.config.AuthRequest;
import com.hossam.onlinechatapp.config.AuthResponse;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.UserRepository;
import com.hossam.onlinechatapp.security.jwt.JwtDecoder;
import com.hossam.onlinechatapp.security.jwt.JwtEncoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;


@Service
public class AuthenticationService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticationService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
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

        User user = userRepository.findUserByEmail(request.getEmail());
        jwtEncoder.encode(user);

        String accessToken  = jwtEncoder.getAccessToken();
        String refreshToken = jwtEncoder.getRefreshToken();

        return new AuthResponse(accessToken, refreshToken);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Check if there is A Bearer token
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return;

        // Extract the refresh token from the header
        final String refreshToken = authHeader.substring(7);
        jwtDecoder.decode(refreshToken);

        // Get subject and username and check if the token valid
        final String username = jwtDecoder.getSubject();
        final Date expirationDate = jwtDecoder.getExpirationDate();
        if (username == null || expirationDate.before(new Date()))
            return;

        // Create a new access token
        User user = userRepository.findUserByEmail(username);
        jwtEncoder.encode(user);
        String accessToken = jwtEncoder.getAccessToken();

        writeAuthResponse(response, accessToken, refreshToken);

    }

    private void writeAuthResponse(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        try (OutputStream outputStream = response.getOutputStream()) {
            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
            new ObjectMapper().writeValue(outputStream, authResponse);
        }
    }

}
