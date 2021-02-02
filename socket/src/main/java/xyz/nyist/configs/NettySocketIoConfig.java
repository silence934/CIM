package xyz.nyist.configs;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Silence
 * @Description:
 * @Date:Create：in 2021/01/4 13:57
 */
@Slf4j
@org.springframework.context.annotation.Configuration
public class NettySocketIoConfig implements CommandLineRunner {

    @Value("${server.port}")
    private String port;

    @Value("${server.address}")
    private String address;

    public static final Map<String, List<SocketIOClient>> CLIENT_MAP = new ConcurrentHashMap<>();


    /**
     * netty-socketIo服务器
     */
    @Bean("socketIoServer")
    public SocketIOServer socketIoServer() {
        Configuration config = new Configuration();
        //单次发送数据最大值1M
        config.setMaxFramePayloadLength(1024 * 1024);
        //socket连接数大小（如只监听一个端口boss线程组为1即可）
        config.setBossThreads(1);
        config.setWorkerThreads(100);
        //身份验证
        config.setAuthorizationListener(handshakeData -> true);
        config.setHostname(address);
        config.setPort(Integer.parseInt(port) + 1);
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
}
