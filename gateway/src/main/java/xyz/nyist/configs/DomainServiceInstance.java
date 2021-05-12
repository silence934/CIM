package xyz.nyist.configs;

import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

/**
 * @author: silence
 * @Date: 2021/5/12 18:10
 * @Description:
 */
public class DomainServiceInstance implements ServiceInstance {
    private static final String HOST_NAME_KEY = "hostname";

    private final ServiceInstance delegate;

    private final String overrideScheme;

    public DomainServiceInstance(ServiceInstance delegate, String overrideScheme) {
        this.delegate = delegate;
        this.overrideScheme = overrideScheme;
    }

    @Override
    public String getServiceId() {
        return delegate.getServiceId();
    }

    @Override
    public String getHost() {
        String host;
        if ((host = getMetadata().get(HOST_NAME_KEY)) == null) {
            host = delegate.getHost();
        }
        return host;
    }

    @Override
    public int getPort() {
        return delegate.getPort();
    }

    @Override
    public boolean isSecure() {
        // TODO: move to map
        if ("https".equals(this.overrideScheme) || "wss".equals(this.overrideScheme)) {
            return true;
        }
        return delegate.isSecure();
    }

    @Override
    public URI getUri() {
        return delegate.getUri();
    }

    @Override
    public Map<String, String> getMetadata() {
        return delegate.getMetadata();
    }

    @Override
    public String getScheme() {
        String scheme = delegate.getScheme();
        if (scheme != null) {
            return scheme;
        }
        return this.overrideScheme;
    }
}
