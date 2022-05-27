package com.prgrms.devcourse.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class JwtSecurityContextRepository implements SecurityContextRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String headerKey;

    private final Jwt jwt;

    public JwtSecurityContextRepository(String headerKey, Jwt jwt) {
        this.headerKey = headerKey;
        this.jwt = jwt;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        JwtAuthenticationToken authentication = authenticate(request);
        if (authentication != null) {
            context.setAuthentication(authentication);
        }
        return  context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        /* jwt를 이용한 인증 방식은 stateless하기 때문에 context를 save 할 필요가 없다. */
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        request.getHeader(headerKey);
        JwtAuthenticationToken authentication = authenticate(request);
        return authentication != null;
    }

    private JwtAuthenticationToken authenticate(HttpServletRequest request) {
        String token = getToken(request);
        if (isNotEmpty(token)) {
            try {
                Jwt.Claims claims = jwt.verify(token);
                log.debug("Jwt parse result: {}", claims);

                String username = claims.username;
                List<GrantedAuthority> authorities = getAuthorities(claims);

                if (Objects.nonNull(username) && authorities.size() > 0) {
                    JwtAuthenticationToken authentication
                            = new JwtAuthenticationToken(new JwtAuthentication(token, username), null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    return authentication;
                }
            } catch (Exception e) {
                log.warn("Jwt processing failed: {}", e.getMessage());
            }
        }
        return null;
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (Objects.nonNull(token)) {
            log.debug("Jwt token detected: {}", token);
            return URLDecoder.decode(token, StandardCharsets.UTF_8);
        }
        return null;
    }

    private List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
        String[] roles = claims.roles;
        return roles == null || roles.length == 0
                ? Collections.emptyList()
                : Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
