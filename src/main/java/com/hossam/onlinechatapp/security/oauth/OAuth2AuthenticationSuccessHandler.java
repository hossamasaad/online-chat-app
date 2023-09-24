package com.hossam.onlinechatapp.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hossam.onlinechatapp.config.AuthResponse;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.UserRepository;
import com.hossam.onlinechatapp.security.jwt.JwtEncoder;
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
import java.util.Date;
import java.util.Objects;


@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(JwtEncoder jwtEncoder, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Get the OAuthUser
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("login") + "@chat-app.com";

        // Check if the user already exist or not
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
                    .createdAt(new Date(System.currentTimeMillis()))
                    .build();

            userRepository.save(createdUser);
        }
        // Login with GitHub
        else {
            jwtEncoder.encode(user);
            String accessToken = jwtEncoder.getAccessToken();
            String refreshToken = jwtEncoder.getRefreshToken();

            writeAuthResponse(response, accessToken, refreshToken);
        }

    }

    private void writeAuthResponse(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        try (OutputStream outputStream = response.getOutputStream()) {
            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
            new ObjectMapper().writeValue(outputStream, authResponse);
        }
    }
}
