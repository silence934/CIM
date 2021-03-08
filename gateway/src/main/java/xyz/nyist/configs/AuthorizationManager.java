package xyz.nyist.configs;

import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import xyz.nyist.constant.AuthConstants;

import java.util.*;

/**
 * @Author : fucong
 * @Date: 2021-02-03 11:03
 * @Description :
 */
@Slf4j
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {


    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String path = request.getURI().getPath();

        // 1. 对应跨域的预检请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // 2. token为空拒绝访问
        String token = request.getHeaders().getFirst(AuthConstants.JWT_TOKEN_HEADER);
        if (StringUtils.isEmpty(token)) {
            return Mono.just(new AuthorizationDecision(false));
        }

        // 3.缓存取资源权限角色关系列表

        Map<Object, Object> resourceRolesMap = new HashMap<>();
        Iterator<Object> iterator = resourceRolesMap.keySet().iterator();

        // 4.请求路径匹配到的资源需要的角色权限集合authorities
        List<String> authorities = new ArrayList<>();
        while (iterator.hasNext()) {
            String pattern = (String) iterator.next();
            if (PATH_MATCHER.match(pattern, path)) {
                authorities.addAll(Convert.toList(String.class, resourceRolesMap.get(pattern)));
            }
        }
        authorities.add("permission2");


        return mono.filter(Authentication::isAuthenticated)
                .map(authentication -> {
                    log.info("访问路径: {}", path);
                    log.info("用户权限: {}", authentication.getAuthorities());
                    log.info("资源需要权限: {}", authorities);
                    boolean granted = authentication.getAuthorities().stream()
                            .anyMatch(permission -> authorities.contains(permission.getAuthority()));
                    return new AuthorizationDecision(granted);
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
