package com.repetovskyi.websecurity.dto;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  AuthenticationRequest
  @version  1.0.0 
  @since 11/1/2026 - 09.23
*/

public record AuthenticationRequest(String email, String password) {
}
