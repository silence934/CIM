package xyz.nyist.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.nyist.handler.AuthenticationConverter;
import xyz.nyist.handler.AuthenticationManager;
import xyz.nyist.handler.AuthorizeConfigManager;

import java.util.LinkedList;

/**
 * @Author : fucong
 * @Date: 2021-02-02 17:10
 * @Description :
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationConverter authenticationConverter;

    @Autowired
    private AuthorizeConfigManager authorizeConfigManager;

    @Autowired
    private ServerLogoutSuccessHandler serverLogoutSuccessHandler;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final String[] AUTH_WHITELIST = new String[]{"/login", "/logout"};

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        SecurityWebFilterChain chain = http.formLogin()
                .loginPage("/login")
                // 登录成功handler
                .authenticationSuccessHandler(new ServerAuthenticationSuccessHandler() {
                    @Override
                    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
                        return null;
                    }
                })
                // 登陆失败handler
                .authenticationFailureHandler(new ServerAuthenticationFailureHandler() {
                    @Override
                    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
                        return null;
                    }
                })
                // 无访问权限handler
                .authenticationEntryPoint(new ServerAuthenticationEntryPoint() {
                    @Override
                    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
                        return null;
                    }
                })
                .and()
                .logout()
                // 登出成功handler
                .logoutSuccessHandler(serverLogoutSuccessHandler)
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .authorizeExchange()
                // 白名单放行
                .pathMatchers(AUTH_WHITELIST).permitAll()
                // 访问权限控制
                .anyExchange().access(authorizeConfigManager)
                .and().build();
        // 设置自定义登录参数转换器
        chain.getWebFilters()
                .filter(webFilter -> webFilter instanceof AuthenticationWebFilter)
                .subscribe(webFilter -> {
                    AuthenticationWebFilter filter = (AuthenticationWebFilter) webFilter;
                    filter.setServerAuthenticationConverter(authenticationConverter);
                });
        return chain;
    }

    /**
     * 注册用户信息验证管理器，可按需求添加多个按顺序执行
     *
     * @return ReactiveAuthenticationManager
     */
    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authenticationManager);
        return new DelegatingReactiveAuthenticationManager(managers);
    }


    /**
     * BCrypt密码编码
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
