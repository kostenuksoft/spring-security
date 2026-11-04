package com.repetovskyi.websecurity.service;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  AuthenticationService
  @version  1.1.0
  @since 11/1/2026 - 09.34
*/

import com.repetovskyi.websecurity.dto.AuthenticationRequest;
import com.repetovskyi.websecurity.dto.AuthenticationResponse;
import com.repetovskyi.websecurity.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(Map.of("fullName", user.getFullName()), user);

        return new AuthenticationResponse(token);
    }
}
