package com.repetovskyi.websecurity.config;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  AuditionConfig
  @version  1.0.0 
  @since 9/27/2026 - 20.48
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class AuditionConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
}
