package xyz.nyist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import xyz.nyist.event.ArtifactDeleteEvent;
import xyz.nyist.event.ArtifactUseEvent;

/**
 * @Author : fucong
 * @Date: 2021-03-14 10:21
 * @Description :
 */
@EnableDiscoveryClient
@SpringBootApplication
@RemoteApplicationEventScan(basePackageClasses = {ArtifactDeleteEvent.class, ArtifactUseEvent.class})
public class FileServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

}
