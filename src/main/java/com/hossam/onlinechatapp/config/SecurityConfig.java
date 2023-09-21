package com.hossam.onlinechatapp.config;


import com.hossam.onlinechatapp.security.JwtAuthenticationFilter;
import com.hossam.onlinechatapp.security.LogoutService;
import com.hossam.onlinechatapp.security.OAuth2AuthenticationSuccessHandler;
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


    private final LogoutService logoutService;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2Handler;

    @Autowired
    public SecurityConfig(
            LogoutService logoutService,
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            OAuth2AuthenticationSuccessHandler oAuth2Handler
    ) {
        this.logoutService = logoutService;
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
                    auth.requestMatchers("/api/oauth/**").permitAll();
                    auth.requestMatchers("/api/**").authenticated();
                }
        );

        http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.oauth2Login(
                oAuth -> {
                    oAuth.defaultSuccessUrl("/api/oauth/authenticate");
                    oAuth.authorizationEndpoint(
                            authorization -> authorization.baseUri("/api/oauth")
                    );
                    oAuth.successHandler(oAuth2Handler);
                }
        );

        http.logout(
                logout -> {
                    logout.logoutUrl("/api/logout");
                    logout.addLogoutHandler(logoutService);
                }
        );

        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
