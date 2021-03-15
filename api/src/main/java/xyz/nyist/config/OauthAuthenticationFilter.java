package xyz.nyist.config;

import io.micrometer.core.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.nyist.bo.CimAuthentication;
import xyz.nyist.constant.AuthConstants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author : fucong
 * @Date: 2021-03-07 18:16
 * @Description :
 */
@Slf4j
@Component("OncePerRequestFilter")
public class OauthAuthenticationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!"/actuator/health".equals(request.getRequestURI())) {
            log.info("访问路径: " + request.getRequestURI());
        }
        String tokenInfo = request.getHeader(AuthConstants.TOKEN_INFO);
        if (StringUtils.isNotBlank(tokenInfo)) {
            Authentication authentication = CimAuthentication.converter(tokenInfo);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

}
