package com.prgrms.devcourse.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private String credentials;

    //인증 전에 호출되는 생성자
    public JwtAuthenticationToken(String principal, String credentials) {
        super(null); // 아직 인증된 사용자가 아니기에 null 삽입
        super.setAuthenticated(false); // 위와 동일

        this.principal = principal;
        this.credentials = credentials;
    }

    //인증 완료료 후에사용되는 생성자
    public JwtAuthenticationToken(Object principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);

        this.principal = principal;
        this.credentials = credentials; // 인증 후라 null이 입력 될 수도 있을 것
    }

    @Override
    public String getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted.");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

    @Override
    public String toString() {
        return "JwtAuthenticationToken{" +
                "principal=" + principal +
                ", credentials='" + credentials + '\'' +
                '}';
    }
}
