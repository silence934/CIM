package xyz.nyist.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import xyz.nyist.entity.ArtifactEntity;
import xyz.nyist.event.ArtifactUseEvent;
import xyz.nyist.service.ArtifactService;

/**
 * @Author : fucong
 * @Date: 2021-04-08 14:31
 * @Description :
 */
@Slf4j
@Component
public class ArtifactUseListener implements ApplicationListener<ArtifactUseEvent> {
    @Autowired
    private ArtifactService artifactService;

    @Override
    public void onApplicationEvent(ArtifactUseEvent event) {
        String path = event.getPath();
        String[] split = path.split("\\?id=");
        ArtifactEntity artifact;
        if (split.length == 2) {
            artifact = artifactService.getById(Integer.valueOf(split[1]));
        } else {
            artifact = artifactService.getByPath(split[0]);
        }
        artifact.setUse(true);
        artifactService.update(artifact);
    }
}
