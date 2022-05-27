package com.prgrms.devcourse.user.dto;

public class LoginRequest {

    private String principal;

    private String credentials;

    protected LoginRequest() {
    }

    public String getPrincipal() {
        return principal;
    }

    public String getCredentials() {
        return credentials;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "principal='" + principal + '\'' +
                ", credentials='" + credentials + '\'' +
                '}';
    }
}
