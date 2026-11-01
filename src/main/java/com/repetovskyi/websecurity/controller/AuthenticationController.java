package com.repetovskyi.websecurity.controller;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  AuthenticationController
  @version  1.0.0 
  @since 11/1/2026 - 09.47
*/

import com.repetovskyi.websecurity.dto.AuthenticationRequest;
import com.repetovskyi.websecurity.dto.AuthenticationResponse;
import com.repetovskyi.websecurity.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
