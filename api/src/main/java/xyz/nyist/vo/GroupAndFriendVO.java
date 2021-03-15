package xyz.nyist.vo;

import lombok.Builder;
import lombok.Data;
import xyz.nyist.entity.GroupEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author : fucong
 * @Date: 2021-03-12 13:53
 * @Description :
 */
@Data
@Builder
public class GroupAndFriendVO {
    private Integer id;

    private String name;

    private List<FriendVO> friends;

    public static GroupAndFriendVO forValue(GroupEntity group) {
        return GroupAndFriendVO.builder()
                .id(group.getId())
                .name(group.getName())
                .friends(group.getFriends().stream().map(f -> FriendVO.forValue(f, group.getUser())).collect(Collectors.toList()))
                .build();
    }
}
