package com.repetovskyi.websecurity.config;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  SecurityConfig
  @version  1.1.0
  @since 9/27/2026 - 20.45
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";
    private static final String SUPERADMIN = "SUPERADMIN";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html").permitAll()
                        .requestMatchers("/api/v1/reports/audit").hasRole(SUPERADMIN)
                        .requestMatchers("/api/v1/reports/owners").hasAnyRole(ADMIN, SUPERADMIN)
                        .requestMatchers("/api/v1/reports/summary").hasAnyRole(USER, ADMIN, SUPERADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/devices/**").hasAnyRole(USER, ADMIN, SUPERADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/devices/**").hasAnyRole(ADMIN, SUPERADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/devices/**").hasAnyRole(ADMIN, SUPERADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/devices/**").hasRole(SUPERADMIN)
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}user")
                .roles(USER)
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin")
                .roles(ADMIN)
                .build();

        UserDetails superadmin = User.builder()
                .username("superadmin")
                .password("{noop}superadmin")
                .roles(SUPERADMIN)
                .build();

        return new InMemoryUserDetailsManager(user, admin, superadmin);
    }
}
