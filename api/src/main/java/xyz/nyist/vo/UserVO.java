package xyz.nyist.vo;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import xyz.nyist.entity.UserEntity;

import java.util.Optional;

/**
 * @Author : fucong
 * @Date: 2021-03-12 13:54
 * @Description :
 */
@Data
@SuperBuilder
public class UserVO {

    private Integer id;

    private String username;

    private String nickname;

    private String avatar;

    private String phone;

    private String mail;

    private String sex;


    public static UserVO forValue(UserEntity user) {
        return Optional.ofNullable(user).map(u -> UserVO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nickname(u.getNickname())
                .avatar(u.getAvatar())
                .phone(u.getPhone())
                .mail(u.getMail())
                .sex(u.getSex())
                .build()).orElse(null);
    }
}
