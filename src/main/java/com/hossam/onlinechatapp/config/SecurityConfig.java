package com.hossam.onlinechatapp.config;

import com.hossam.onlinechatapp.security.jwt.JwtAuthenticationFilter;
import com.hossam.onlinechatapp.security.oauth.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2Handler;

    @Autowired
    public SecurityConfig(AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter, OAuth2AuthenticationSuccessHandler oAuth2Handler) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2Handler = oAuth2Handler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(
                auth -> {
                    auth.requestMatchers("/api/auth/**").permitAll();
                    auth.requestMatchers("/api/auth/refresh-token").authenticated();
                    auth.anyRequest().authenticated();
                }
        );

        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        http.oauth2Login(
                oAuth -> {
                    oAuth.authorizationEndpoint(
                            authorization -> authorization.baseUri("/api/auth/github")
                    );
                    oAuth.successHandler(oAuth2Handler);
                }
        );

        http.authenticationProvider(authenticationProvider);

        http.addFilterBefore(
                jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
