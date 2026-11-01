package com.repetovskyi.websecurity.service;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  AuthenticationService
  @version  1.0.0 
  @since 11/1/2026 - 09.34
*/

import com.repetovskyi.websecurity.dto.AuthenticationRequest;
import com.repetovskyi.websecurity.dto.AuthenticationResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        return new AuthenticationResponse(null);
    }
}
