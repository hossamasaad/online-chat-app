package com.hossam.onlinechatapp.security;

import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Objects;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("login") + "@chat-app.com";

        User user = userRepository.findUserByEmail(email);

        if (user == null) {

            String[] name = ((String) Objects.requireNonNull(oauth2User.getAttribute("name"))).split(" ", 2);
            String firstName = name[0];
            String lastName = name[1];
            String password = email + "@secret";

            User createdUser = User.builder()
                    .id(null)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            user = userRepository.save(createdUser);
        }

        response.sendRedirect("/api/auth/token?id=" + user.getId());
    }

}
