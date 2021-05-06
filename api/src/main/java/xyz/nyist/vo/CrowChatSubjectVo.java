package xyz.nyist.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import xyz.nyist.entity.CrowdEntity;

import java.util.Optional;

/**
 * @author: silence
 * @Date: 2021/5/5 16:01
 * @Description:
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CrowChatSubjectVo extends ChatSubjectVo{

    public static CrowChatSubjectVo forValue(CrowdEntity crowd) {
        return Optional.ofNullable(crowd).map(u -> CrowChatSubjectVo.builder()
                .id(u.getId())
                .name(u.getName())
                .avatar(u.getAvatar())
                .build()).orElse(null);
    }
}
