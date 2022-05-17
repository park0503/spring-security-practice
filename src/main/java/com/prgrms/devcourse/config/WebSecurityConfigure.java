package com.prgrms.devcourse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        auth
                .inMemoryAuthentication()
                .withUser("user")
                .password("{noop}user123")
                .roles("USER")
                .and()

                .withUser("admin")
                .password("{noop}admin123")
                .roles("ADMIN");
    }

    @Override
    //필터 체인 관련 전역 설정을 처리할 수 있는 API 제공)
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/assets/**");
        // 지정된 ant Path에 매칭되는 요청에 대해서는 시큐리티 필터체인을 태우지 않겠다.
        //불필요한 자원 소모 방지
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()//공개 리소스, 혹은 보호받는 리소스에 대해 세부 설정을 할 수 있는 부분
                .antMatchers("/me").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll()
                .and()

                .formLogin()//스프링 시큐리티가 자동으로 로그인 페이지를 생성해 주는 것을 생성
                .defaultSuccessUrl("/me") //성공 시 리다이렉트 페이지
                .permitAll() //로그인 페이지는 익명 페이지
                .and()

                .logout()
                // 아래 매쳐는 default 값과 똑같지만 없으면 한번 더 물어봄
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)// default라 없어도 됨
//                .clearAuthentication(true)// default라 없어도 됨
                .and()

                .rememberMe()
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(300)
                .and()

                .requiresChannel()
                .anyRequest()
                .requiresSecure()
        ;
    }
}
