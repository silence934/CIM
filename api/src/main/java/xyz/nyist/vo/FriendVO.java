package xyz.nyist.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import xyz.nyist.entity.FriendEntity;
import xyz.nyist.entity.UserEntity;

/**
 * @Author : fucong
 * @Date: 2021-03-12 17:50
 * @Description :
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class FriendVO extends UserVO {
    private String remark;

    private Integer friendId;


    public static FriendVO forValue(FriendEntity friend, UserEntity user) {
        if (friend.getUserOne().equals(user)) {
            UserEntity userEntity = friend.getUserTwo();
            return FriendVO.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .nickname(userEntity.getNickname())
                    .avatar(userEntity.getAvatar())
                    .phone(userEntity.getPhone())
                    .mail(userEntity.getMail())
                    .remark(friend.getRemarkTwo())
                    .sex(user.getSex())
                    .friendId(friend.getId())
                    .build();
        } else {
            UserEntity userEntity = friend.getUserOne();
            return FriendVO.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .nickname(userEntity.getNickname())
                    .avatar(userEntity.getAvatar())
                    .phone(userEntity.getPhone())
                    .mail(userEntity.getMail())
                    .remark(friend.getRemarkOne())
                    .sex(user.getSex())
                    .friendId(friend.getId())
                    .build();
        }


    }
}
