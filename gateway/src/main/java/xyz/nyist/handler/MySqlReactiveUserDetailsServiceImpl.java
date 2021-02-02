package xyz.nyist.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @Author : fucong
 * @Date: 2021-02-02 17:50
 * @Description :
 */
@Slf4j
@Component
public class MySqlReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private static final String USER_NOT_EXISTS = "用户不存在！";


    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.error(new UsernameNotFoundException(USER_NOT_EXISTS));
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        throw new UsernameNotFoundException(USER_NOT_EXISTS);
    }
}
