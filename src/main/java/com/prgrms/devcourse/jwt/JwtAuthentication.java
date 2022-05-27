package com.prgrms.devcourse.jwt;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

//인증 완료 후 인증된 사용자를 표현하기 위한 객체
public class JwtAuthentication {
    public final String token;

    public final String username;

    public JwtAuthentication(String token, String username) {
        checkArgument(isNotEmpty(token), "token must be provided");
        checkArgument(isNotEmpty(username), "username must be provided");

        this.token = token;
        this.username = username;
    }

    @Override
    public String toString() {
        return "JwtAuthentication{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
