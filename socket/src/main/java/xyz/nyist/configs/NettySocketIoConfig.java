package xyz.nyist.configs;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import xyz.nyist.constant.RedisKey;

import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Silence
 * @Description: , ApplicationListener<ContextClosedEvent>
 * @Date:Create：in 2021/01/4 13:57
 */
@Slf4j
@org.springframework.context.annotation.Configuration
public class NettySocketIoConfig implements CommandLineRunner {

    @Autowired
    private RedissonClient redissonClient;

    @Value("${spring.cloud.consul.discovery.port}")
    private Integer port;

    @Value("${server.address}")
    private String address;

    public static final Map<Integer, List<SocketIOClient>> CLIENT_MAP = new ConcurrentHashMap<>();


    /**
     * netty-socketIo服务器
     */
    @Bean("socketIoServer")
    public SocketIOServer socketIoServer() {
        Configuration config = new Configuration();
        //单次发送数据最大值1M
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);
        //socket连接数大小（如只监听一个端口boss线程组为1即可）
        config.setBossThreads(1);
        config.setWorkerThreads(100);
        //身份验证
        config.setAuthorizationListener(handshakeData -> {
            HttpHeaders httpHeaders = handshakeData.getHttpHeaders();
            System.out.println(handshakeData.getHttpHeaders());
            return true;
        });
        config.setHostname(address);
        config.setPort(port);

        config.setKeyStorePassword("fc2998820...");
        InputStream stream = this.getClass().getResourceAsStream("/www.nyist.xyz.jks");
        config.setKeyStore(stream);

        return new SocketIOServer(config);
    }

    /**
     * 用于扫描netty-socketIo的注解，比如 @OnConnect、@OnEvent
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {
        return new SpringAnnotationScanner(socketIoServer());
    }


    @Override
    public void run(String... strings) {
        SocketIOServer server = socketIoServer();
        server.start();
        log.info("socket.io启动成功！");
    }

    @PreDestroy
    public void destroy() {
        log.info("关闭服务");
        NettySocketIoConfig.CLIENT_MAP.forEach((k, v) -> redissonClient.getAtomicLong(RedisKey.USER_ONLINE_KEY + k).getAndAdd(-v.size()));
    }
}
