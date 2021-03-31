package xyz.nyist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import xyz.nyist.event.MailEvent;

/**
 * @Author : fucong
 * @Date: 2021-03-30 13:40
 * @Description :
 */
@EnableAsync
@Configuration
@EnableDiscoveryClient
@SpringBootApplication
@RemoteApplicationEventScan(basePackageClasses = {MailEvent.class})
public class MailApplication {

    public static void main(String[] args) {

        SpringApplication.run(MailApplication.class, args);
    }
}
