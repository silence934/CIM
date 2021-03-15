package xyz.nyist.vo;

import lombok.Builder;
import lombok.Data;
import xyz.nyist.entity.ArtifactEntity;

/**
 * @Author : fucong
 * @Date: 2021-03-14 10:49
 * @Description :
 */
@Data
@Builder
public class ArtifactVO {

    private Integer id;

    private String path;

    private String fileName;

    public static ArtifactVO forValue(ArtifactEntity artifactEntity) {
        return ArtifactVO.builder()
                .id(artifactEntity.getId())
                .path(artifactEntity.getPath() + "?id=" + artifactEntity.getId())
                .fileName(artifactEntity.getFileName())
                .build();
    }
}
