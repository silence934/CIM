package xyz.nyist.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @author: silence
 * @Date: 2021/5/12 15:26
 * @Description:
 */
@Slf4j
@Component
public class DomainLoadBalancerClientFilter implements GlobalFilter, Ordered {

    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10100;

    protected final LoadBalancerClient loadBalancer;

    private final LoadBalancerProperties properties;

    public DomainLoadBalancerClientFilter(LoadBalancerClient loadBalancer, LoadBalancerProperties properties) {
        this.loadBalancer = loadBalancer;
        this.properties = properties;
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    protected ServiceInstance choose(ServerWebExchange exchange) {
        return loadBalancer.choose(
                ((URI) exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR)).getHost());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || (!"hs".equals(url.getScheme()) && !"hs".equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        addOriginalRequestUrl(exchange, url);

        if (log.isTraceEnabled()) {
            log.trace("LoadBalancerClientFilter url before: " + url);
        }

        final ServiceInstance instance = choose(exchange);

        if (instance == null) {
            throw NotFoundException.create(properties.isUse404(),
                    "Unable to find instance for " + url.getHost());
        }

        URI uri = exchange.getRequest().getURI();

        String overrideScheme = instance.isSecure() ? "https" : "http";
        if (schemePrefix != null) {
            overrideScheme = url.getScheme();
        }

        URI requestUrl = loadBalancer.reconstructURI(
                new DomainServiceInstance(instance, overrideScheme), uri);

        if (log.isTraceEnabled()) {
            log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
        }

        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        return chain.filter(exchange);
    }


}
