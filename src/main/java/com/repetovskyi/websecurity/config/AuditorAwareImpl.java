package com.repetovskyi.websecurity.config;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  AuditorAwareImpl
  @version  1.0.0 
  @since 9/27/2026 - 20.52
*/

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String SYSTEM = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of(SYSTEM);
        }

        return Optional.of(authentication.getName());
    }
}
