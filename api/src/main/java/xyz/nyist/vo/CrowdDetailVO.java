package xyz.nyist.vo;

import lombok.Builder;
import lombok.Data;
import xyz.nyist.entity.CrowdEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author : fucong
 * @Date: 2021-03-20 18:54
 * @Description :
 */
@Data
@Builder
public class CrowdDetailVO {

    private Integer id;

    private String name;

    private String avatar;

    private Integer lordId;

    private String announcement;

    private List<UserVO> users;

    public static CrowdDetailVO forValue(CrowdEntity crowdEntity) {
        return Optional.ofNullable(crowdEntity).map(c ->
                CrowdDetailVO.builder().id(c.getId())
                        .lordId(c.getLordId())
                        .name(c.getName())
                        .avatar(c.getAvatar())
                        .users(c.getUsers().stream().map(UserVO::forValue).collect(Collectors.toList()))
                        .announcement(c.getAnnouncement())
                        .build()).orElse(null);
    }
}
