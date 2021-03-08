package xyz.nyist.configs;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.nyist.bo.CimAuthentication;
import xyz.nyist.constant.AuthConstants;
import xyz.nyist.utils.JsonUtil;

import java.util.Collection;
import java.util.Map;

/**
 * @Author : fucong
 * @Date: 2021-03-07 13:04
 * @Description :
 */
@Component
public class JwtTokenGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    ServerHttpRequest tokenRequest = exchange
                            .getRequest()
                            .mutate().header(AuthConstants.TOKEN_INFO, converter(authentication))
                            .build();
                    ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
                    return chain.filter(build);
                }).switchIfEmpty(chain.filter(exchange));
    }

    @SuppressWarnings("unchecked")
    private String converter(Authentication authentication) {
        Map<String, Object> attributes = ((JwtAuthenticationToken) authentication).getTokenAttributes();
        CimAuthentication cimAuthentication = CimAuthentication.builder()
                .authorities((Collection<GrantedAuthority>) attributes.get("authorities"))
                .id(((Long) attributes.get("id")).intValue())
                .userName((String) attributes.get("user_name"))
                .build();
        return JsonUtil.obj2String(cimAuthentication);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
