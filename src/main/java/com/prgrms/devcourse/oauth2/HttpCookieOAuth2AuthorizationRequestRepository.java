package com.prgrms.devcourse.oauth2;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository {

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME";

    private final String cookieName;

    private final int cookieExpireSeconds;

    public HttpCookieOAuth2AuthorizationRequestRepository() {
        this(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, 180);
    }

    public HttpCookieOAuth2AuthorizationRequestRepository(String cookieName, int cookieExpireSeconds) {
        this.cookieName = cookieName;
        this.cookieExpireSeconds = cookieExpireSeconds;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return getCookie(request)
                .map(this::getOAuth2AuthorizationRequest)
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            getCookie(request).ifPresent(cookie -> clear(cookie, response));
        } else {
            String value = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(authorizationRequest));
            Cookie cookie = new Cookie(cookieName, value);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(cookieExpireSeconds);
            response.addCookie(cookie);
        }
    }

    //이 메소드는 사실 안쓰인다. 밑에 response까지 같이 인자로 받는 놈이 쓰임
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return getCookie(request)
                .map(cookie -> {
                    OAuth2AuthorizationRequest oAuth2Request = getOAuth2AuthorizationRequest(cookie);
                    clear(cookie, response);
                    return oAuth2Request;
                })
                .orElse(null);
    }

    private Optional<Cookie> getCookie(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, cookieName));
    }

    private void clear(Cookie cookie, HttpServletResponse response) {
        cookie.setValue("");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private OAuth2AuthorizationRequest getOAuth2AuthorizationRequest(Cookie cookie) {
        return SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue()));
    }
}
