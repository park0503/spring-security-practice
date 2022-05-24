package com.prgrms.devcourse.user.service;

import com.prgrms.devcourse.user.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByLoginId(username)
                .map(u -> User.builder()
                        .username(u.getLoginId())
                        .password(u.getPasswd())
                        .authorities(u.getGroup().getAuthorities())
                        .build())
                .orElseThrow(
                        () -> new UsernameNotFoundException("Username " + username + " not found")
                );
    }
}
