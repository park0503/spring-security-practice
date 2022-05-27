package com.prgrms.devcourse.config;

import com.prgrms.devcourse.jwt.Jwt;
import com.prgrms.devcourse.jwt.JwtAuthenticationFilter;
import com.prgrms.devcourse.jwt.JwtAuthenticationProvider;
import com.prgrms.devcourse.jwt.JwtSecurityContextRepository;
import com.prgrms.devcourse.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JwtConfigure jwtConfigure;

    public WebSecurityConfigure(JwtConfigure jwtConfigure) {
        this.jwtConfigure = jwtConfigure;
    }


    @Override
    //필터 체인 관련 전역 설정을 처리할 수 있는 API 제공)
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/assets/**", "/h2-console/**");
        // 지정된 ant Path에 매칭되는 요청에 대해서는 시큐리티 필터체인을 태우지 않겠다.
        //불필요한 자원 소모 방지
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Jwt jwt() {
        return new Jwt(
                jwtConfigure.getIssuer(),
                jwtConfigure.getClientSecret(),
                jwtConfigure.getExpirySeconds()
        );
    }

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider(Jwt jwt, UserService userService) {
        return new JwtAuthenticationProvider(jwt, userService);
    }

//    @Autowired
//    public void configureAuthentication(AuthenticationManagerBuilder builder, JwtAuthenticationProvider provider) {
//        builder.authenticationProvider(provider);
//    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, e) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication != null ? authentication.getPrincipal() : null;
            log.warn("{} is denied", principal, e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain");
            response.getWriter().write("## ACCESS DENIED ##");
            response.getWriter().flush();
            response.getWriter().close();
        };
    }

    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        Jwt jwt = getApplicationContext().getBean(Jwt.class);
        return new JwtAuthenticationFilter(jwtConfigure.getHeader(), jwt);
    }

    public SecurityContextRepository securityContextRepository() {
        Jwt jwt = getApplicationContext().getBean(Jwt.class);
        return new JwtSecurityContextRepository(jwtConfigure.getHeader(), jwt);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()//공개 리소스, 혹은 보호받는 리소스에 대해 세부 설정을 할 수 있는 부분
                .antMatchers("/api/user/me").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll()
                .and()
                .csrf()
                .disable()
                .formLogin()//스프링 시큐리티가 자동으로 로그인 페이지를 생성해 주는 것을 생성
                .disable()
                .headers()
                .disable()
                .logout()
                .disable()
                .rememberMe()
                .disable()
                .httpBasic()
                .disable()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())

                .and()
                .securityContext()
                .securityContextRepository(securityContextRepository())

                .and()
                .addFilterAfter(jwtAuthenticationFilter(), SecurityContextPersistenceFilter.class)
        ;
    }
}
