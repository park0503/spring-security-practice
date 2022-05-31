package com.prgrms.devcourse.oauth2;

import com.prgrms.devcourse.jwt.Jwt;
import com.prgrms.devcourse.user.User;
import com.prgrms.devcourse.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Jwt jwt;

    private final UserService userService;

    public OAuth2AuthenticationSuccessHandler(Jwt jwt, UserService userService) {
        this.jwt = jwt;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws ServletException, IOException {
        /**
         * JWT 토큰을 만들어서 응답
         * 사용자를 가입시키는 처리 (이미 가입되었다면 아무 처리하지 않음)
         */
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oAuth2Token.getPrincipal();
            String provider = oAuth2Token.getAuthorizedClientRegistrationId(); // 카카오라면 요서 값이 들어옴
            User user = processUserOAuth2UserJoin(oAuth2User, provider);
            String loginSuccessJson = generateLoginSuccessJson(user);
            response.setContentType("application/json;charset=UTF-8");
            response.setContentLength(loginSuccessJson.getBytes(StandardCharsets.UTF_8).length);
            response.getWriter().write(loginSuccessJson);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private User processUserOAuth2UserJoin(OAuth2User oAuth2User, String provider) {
        return userService.join(oAuth2User, provider);
    }

    private String generateLoginSuccessJson(User user) {
        String token = generateToken(user);
        log.debug("Jwt({}) created for oauth2 login user {}", token, user.getUsername());
        return "{\"token\":\"" + token + "\", \"username\":\"" + user.getUsername() + "\", \"group\":\"" + user.getGroup().getName() + "\"}";
    }

    private String generateToken(User user) {
        return jwt.sign(Jwt.Claims.from(user.getUsername(), new String[]{"ROLE_USER"}));
    }
}
