package xyz.nyist.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import xyz.nyist.entity.FriendEntity;
import xyz.nyist.entity.UserEntity;

import java.util.Optional;

/**
 * @author: silence
 * @Date: 2021/5/5 16:01
 * @Description:
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserChatSubjectVo extends ChatSubjectVo{


    public static CrowChatSubjectVo forValue(UserEntity user, FriendEntity friend) {
        String name=null;
        if (friend!=null){
            if (friend.getUserOne().equals(user)){
                name=friend.getRemarkOne();
            }else {
                name=friend.getRemarkTwo();
            }
        }
        if (name==null) {
            if (user.getNickname() != null) {
                name = user.getNickname();
            } else {
                name = user.getUsername();
            }
        }

        String finalName = name;
        return Optional.ofNullable(user).map(u -> CrowChatSubjectVo.builder()
                .id(u.getId())
                .name(finalName)
                .avatar(u.getAvatar())
                .build()).orElse(null);
    }
}
