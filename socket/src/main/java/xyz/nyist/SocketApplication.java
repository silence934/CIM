package xyz.nyist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import xyz.nyist.event.MessageEvent;

/**
 * @Author : fucong
 * @Date: 2021-01-29 20:51
 * @Description :
 */
@Configuration
@EnableDiscoveryClient
@SpringBootApplication
@RemoteApplicationEventScan(basePackageClasses = MessageEvent.class)
public class SocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketApplication.class, args);
    }

}
