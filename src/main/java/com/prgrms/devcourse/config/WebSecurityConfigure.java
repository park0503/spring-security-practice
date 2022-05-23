package com.prgrms.devcourse.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
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
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DataSource dataSource;

    public WebSecurityConfigure(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @Qualifier("myAsyncTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("my-executor-");
        return executor;
    }

    @Bean
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(
            @Qualifier("myAsyncTaskExecutor") AsyncTaskExecutor delegate) {
        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
//        //SELECT login_id, passwd, true FROM USERS where users.login_id = 'user';
//        jdbcDao.setDataSource(dataSource);
//        jdbcDao.setEnableAuthorities(false);
//        jdbcDao.setEnableGroups(true);
//        jdbcDao.setUsersByUsernameQuery(
//                "SELECT " +
//                "login_id, passwd, true " +
//                "FROM " +
//                "users " +
//                "WHERE " +
//                "login_id = ?"
//        );
//        jdbcDao.setGroupAuthoritiesByUsernameQuery(
//                "SELECT " +
//                "u.login_id, g.name, p.name " +
//                "FROM " +
//                "users u JOIN groups g ON u.group_id = g.id " +
//                "LEFT JOIN group_permission gp ON g.id = gp.group_id " +
//                "JOIN permissions p ON p.id = gp.permission_id " +
//                "WHERE " +
//                "u.login_id = ?"
//        );
//        return jdbcDao;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("SELECT " +
                        "login_id, passwd, true " +
                        "FROM " +
                        "users " +
                        "WHERE " +
                        "login_id = ?")
                .groupAuthoritiesByUsername("SELECT " +
                        "u.login_id, g.name, p.name " +
                        "FROM " +
                        "users u JOIN groups g ON u.group_id = g.id " +
                        "LEFT JOIN group_permission gp ON g.id = gp.group_id " +
                        "JOIN permissions p ON p.id = gp.permission_id " +
                        "WHERE " +
                        "u.login_id = ?")
                .getUserDetailsService()
                .setEnableAuthorities(false);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    //필터 체인 관련 전역 설정을 처리할 수 있는 API 제공)
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/assets/**", "/h2-console/**");
        // 지정된 ant Path에 매칭되는 요청에 대해서는 시큐리티 필터체인을 태우지 않겠다.
        //불필요한 자원 소모 방지
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> voters = new ArrayList<>();
        voters.add(new WebExpressionVoter());
        voters.add(new OddAdminVoter(new AntPathRequestMatcher("/admin")));
        return new UnanimousBased(voters);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()//공개 리소스, 혹은 보호받는 리소스에 대해 세부 설정을 할 수 있는 부분
                .antMatchers("/me").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin").access("hasRole('ADMIN') and isFullyAuthenticated()") //isFullyAuthenticated 는 remember me 사용 불가
                .anyRequest().permitAll()
//                .accessDecisionManager(accessDecisionManager())
//                .expressionHandler(securityExpressionHandler())
                .and()

                .formLogin()//스프링 시큐리티가 자동으로 로그인 페이지를 생성해 주는 것을 생성
                .defaultSuccessUrl("/") //성공 시 리다이렉트 페이지
//                .loginPage("my-login")
//                .usernameParameter("my-username")
//                .passwordParameter("my-password")
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
                .and()

                .sessionManagement()
                .sessionFixation()
                .changeSessionId()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .and()
                .and()

                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
        ;
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
}
