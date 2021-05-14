package xyz.nyist.vo;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import xyz.nyist.entity.CrowdEntity;
import xyz.nyist.entity.UserEntity;

/**
 * @author: silence
 * @Date: 2021/5/5 15:58
 * @Description:
 */
@Data
@SuperBuilder
public class ChatSubjectVo {

    private Integer id;

    private String name;

    private String avatar;


    public static ChatSubjectVo forValue(UserEntity userEntity) {
        String name;
        if (userEntity.getNickname() != null) {
            name = userEntity.getNickname();
        } else {
            name = userEntity.getUsername();
        }
        return ChatSubjectVo.builder()
                .id(userEntity.getId())
                .name(name)
                .avatar(userEntity.getAvatar())
                .build();
    }


    public static ChatSubjectVo forValue(CrowdEntity crowdEntity) {
        return ChatSubjectVo.builder()
                .id(crowdEntity.getId())
                .name(crowdEntity.getName())
                .avatar(crowdEntity.getAvatar())
                .build();
    }
}
