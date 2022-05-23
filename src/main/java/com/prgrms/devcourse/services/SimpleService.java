package com.prgrms.devcourse.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class SimpleService {
    public final Logger log = LoggerFactory.getLogger(getClass());

    @Async
    public String asyncMethod() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = authentication != null ? (User) authentication.getPrincipal() : null;
        String name = principal != null ? principal.getUsername() : null;
        log.info("asyncMethod result: {}", name);
        return name;
    }
}
