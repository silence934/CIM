package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.nyist.entity.FriendEntity;
import xyz.nyist.entity.GroupEntity;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.FriendRepository;
import xyz.nyist.result.ResultCode;

/**
 * @Author : fucong
 * @Date: 2021-03-12 14:37
 * @Description :
 */
@Slf4j
@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    public FriendEntity getById(Integer id) {
        return friendRepository.findById(id)
                .orElseThrow(() -> new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "朋友[id:%s]不存在", id));
    }

    public FriendEntity getFriend(UserEntity user1,UserEntity user2){
        return friendRepository.getByUser(user1,user2);
    }

    public void create(Integer userId1, Integer userId2, String remark1, String remark2, Integer groupId1, Integer groupId2) {
        UserEntity user1 = userService.getById(userId1);
        UserEntity user2 = userService.getById(userId2);
        GroupEntity group1 = groupService.getById(groupId1);
        GroupEntity group2 = groupService.getById(groupId2);

        friendRepository.saveAndFlush(FriendEntity.builder()
                .userOne(user1)
                .userTwo(user2)
                .remarkOne(remark1)
                .groupOne(group1)
                .groupTwo(group2)
                .remarkTwo(remark2)
                .build());
    }

    public void update(Integer id, Integer userId, String remark, Integer groupId) {
        GroupEntity group = groupService.getById(groupId);
        FriendEntity friend = getById(id);
        if (friend.getUserOne().getId().equals(userId)) {
            friend.setRemarkOne(remark);
            friend.setGroupOne(group);
        } else {
            friend.setRemarkTwo(remark);
            friend.setGroupTwo(group);
        }
        friendRepository.saveAndFlush(friend);
    }

    public void update(FriendEntity friend) {
        friendRepository.saveAndFlush(friend);
    }

    public void delete(Integer id) {
        friendRepository.deleteById(id);
    }


}
