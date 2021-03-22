package xyz.nyist.vo;

import lombok.Builder;
import lombok.Data;
import xyz.nyist.entity.CrowdEntity;

import java.util.Optional;

/**
 * @Author : fucong
 * @Date: 2021-03-16 11:31
 * @Description :
 */
@Data
@Builder
public class CrowdVO {
    private Integer id;

    private String name;

    private String avatar;

    private Integer lordId;


    public static CrowdVO forValue(CrowdEntity crowdEntity) {
        return Optional.ofNullable(crowdEntity).map(c ->
                CrowdVO.builder().id(c.getId())
                        .lordId(c.getLordId())
                        .name(c.getName())
                        .avatar(c.getAvatar())
                        .build()).orElse(null);
    }
}
