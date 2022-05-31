package com.prgrms.devcourse.user.controller;

import com.prgrms.devcourse.jwt.JwtAuthentication;
import com.prgrms.devcourse.jwt.JwtAuthenticationToken;
import com.prgrms.devcourse.user.User;
import com.prgrms.devcourse.user.dto.UserDto;
import com.prgrms.devcourse.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;


    public UserRestController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/users/login")
//    public UserDto login(@RequestBody LoginRequest request) {
//        JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getPrincipal(),
//                request.getCredentials());
//        Authentication resultToken = authenticationManager.authenticate(authToken);
//        JwtAuthenticationToken authenticated = (JwtAuthenticationToken) resultToken;
//        JwtAuthentication principal = (JwtAuthentication) authenticated.getPrincipal();
//        User user = (User) authenticated.getDetails();
//        return new UserDto(principal.token, principal.username, user.getGroup().getName());
//    }
//
    @GetMapping("/users/me")
    public UserDto me(@AuthenticationPrincipal JwtAuthentication authentication) {
        return userService.findByUsername(authentication.username)
                .map(user ->
            new UserDto(authentication.token, authentication.username, user.getGroup().getName()))
                .orElseThrow(() -> new IllegalArgumentException(("Could not found user for " + authentication.username)));
    }

}
