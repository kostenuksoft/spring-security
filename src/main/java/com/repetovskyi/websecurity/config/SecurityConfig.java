package com.repetovskyi.websecurity.config;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  SecurityConfig
  @version  1.3.0
  @since 9/27/2026 - 20.45
*/

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private static final String USER = "USER";
//    private static final String ADMIN = "ADMIN";
//    private static final String SUPERADMIN = "SUPERADMIN";

    private final AuthenticationProvider authenticationProvider;

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/api/v1/auth/**").permitAll()
//                        .requestMatchers("/api/v1/reports/audit").hasRole(SUPERADMIN)
//                        .requestMatchers("/api/v1/reports/owners").hasAnyRole(ADMIN, SUPERADMIN)
//                        .requestMatchers("/api/v1/reports/summary").hasAnyRole(USER, ADMIN, SUPERADMIN)
//                        .requestMatchers(HttpMethod.GET, "/api/v1/devices/**").hasAnyRole(USER, ADMIN, SUPERADMIN)
//                        .requestMatchers(HttpMethod.POST, "/api/v1/devices/**").hasAnyRole(ADMIN, SUPERADMIN)
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/devices/**").hasAnyRole(ADMIN, SUPERADMIN)
//                        .requestMatchers(HttpMethod.DELETE, "/api/v1/devices/**").hasRole(SUPERADMIN)
                        .anyRequest().authenticated())
//                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider);

        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails user = User.builder()
//                .username("user")
//                .password(passwordEncoder.encode("user"))
//                .roles(USER)
//                .build();
//
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("admin"))
//                .roles(ADMIN)
//                .build();
//
//        UserDetails superadmin = User.builder()
//                .username("superadmin")
//                .password(passwordEncoder.encode("superadmin"))
//                .roles(SUPERADMIN)
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin, superadmin);
//    }
}
