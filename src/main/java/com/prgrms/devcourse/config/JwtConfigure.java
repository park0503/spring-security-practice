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

    public void setHeader(String header) {
        this.header = header;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setExpirySeconds(int expirySeconds) {
        this.expirySeconds = expirySeconds;
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
