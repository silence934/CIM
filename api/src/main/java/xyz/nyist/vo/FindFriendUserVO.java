package xyz.nyist.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import xyz.nyist.entity.UserEntity;

import java.util.Optional;

/**
 * @Author : fucong
 * @Date: 2021-03-21 14:16
 * @Description :
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FindFriendUserVO extends UserVO {

    private Boolean isExist;


    public static FindFriendUserVO forValue(UserEntity user, Boolean isExist) {
        return Optional.ofNullable(user).map(u -> FindFriendUserVO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nickname(u.getNickname())
                .avatar(u.getAvatar())
                .phone(u.getPhone())
                .mail(u.getMail())
                .sex(u.getSex())
                .isExist(isExist)
                .build()).orElse(null);
    }
}
