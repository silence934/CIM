package xyz.nyist.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import xyz.nyist.event.ArtifactDeleteEvent;
import xyz.nyist.service.ArtifactService;

import java.io.File;

/**
 * @Author : fucong
 * @Date: 2021-04-08 14:31
 * @Description :
 */
@Slf4j
@Component
public class ArtifactDeleteListener implements ApplicationListener<ArtifactDeleteEvent> {
    @Value("${file-path}")
    private String path;
    @Autowired
    private ArtifactService artifactService;

    private static final String key = "/proxy/api-v1/artifact";

    @Override
    public void onApplicationEvent(ArtifactDeleteEvent event) {
        String path = event.getPath();
        String[] split = path.split("\\?id=");
        if (split[0].startsWith(key)) {
            split[0] = split[0].substring(key.length());
        }
        log.warn("删除文件" + split[0] + "----" + new File(this.path + split[0]).delete());
        if (split.length == 2) {
            artifactService.deleteById(Integer.valueOf(split[1]));
        } else {
            artifactService.deleteByPath(split[0]);
        }

    }
}
