package xyz.nyist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import xyz.nyist.event.MailEvent;
import xyz.nyist.event.MessageEvent;

/**
 * @Author : fucong
 * @Date: 2021-03-06 15:45
 * @Description :
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@RemoteApplicationEventScan(basePackageClasses = {MailEvent.class, MessageEvent.class})
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
