package com.prgrms.devcourse.controllers;

import com.prgrms.devcourse.services.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.Callable;

@Controller
public class SimpleController {
    public final Logger log = LoggerFactory.getLogger(getClass());

    private final SimpleService simpleService;

    public SimpleController(SimpleService simpleService) {
        this.simpleService = simpleService;
    }

    @GetMapping(path = "/asyncHello")
    @ResponseBody
    public Callable<String> asyncHello() {
        log.info("[Before callable] asyncHello started.");
        Callable<String> callable = () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User principal = authentication != null ? (User) authentication.getPrincipal() : null;
            String name = principal != null ? principal.getUsername() : null;
            log.info("[Inside callable] Hello {}", name);
            return "Hello " + name;
        };
        log.info("[After callable] asyncHello completed.");
        return callable;
    }

    @GetMapping(path = "/someMethod")
    @ResponseBody
    public String someMethod() {
        log.info("someMethod started.");
        simpleService.asyncMethod();
        log.info("someMethod completed.");
        return "OK";
    }
}
