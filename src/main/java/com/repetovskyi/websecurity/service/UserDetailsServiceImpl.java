package com.repetovskyi.websecurity.service;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  UserDetailsServiceImpl
  @version  1.1.0
  @since 11/1/2026 - 08.49
*/

import com.repetovskyi.websecurity.model.Role;
import com.repetovskyi.websecurity.model.User;
import com.repetovskyi.websecurity.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    void init() {
        userRepository.deleteAll();
        userRepository.saveAll(List.of(
                user("1", "Olena", "Koval", "k.olena@registry.local", "user", Role.USER),
                user("2", "Taras", "Melnyk", "m.taras@registry.local", "admin", Role.ADMIN),
                user("3", "Vladyslav", "Repetovskyi", "r.vladyslav@registry.local", "superadmin", Role.SUPERADMIN)
        ));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private User user(String id, String firstName, String lastName, String email, String password, Role role) {
        return User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .accountLocked(false)
                .roles(List.of(role))
                .build();
    }
}
