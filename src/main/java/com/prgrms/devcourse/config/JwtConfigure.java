package com.prgrms.devcourse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigure {

    private String header;

    private String issuer;

    private String clientSecret;

    private int expirySeconds;

    public String getHeader() {
        return header;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public int getExpirySeconds() {
        return expirySeconds;
    }

    @Override
    public String toString() {
        return "JwtConfigure{" +
                "header='" + header + '\'' +
                ", issuer='" + issuer + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", expirySeconds=" + expirySeconds +
                '}';
    }
}
