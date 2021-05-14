package xyz.nyist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nyist.component.UserContext;
import xyz.nyist.constant.MessageStatus;
import xyz.nyist.entity.MessageEntity;
import xyz.nyist.result.Result;
import xyz.nyist.service.FriendService;
import xyz.nyist.service.GroupService;
import xyz.nyist.service.MessageService;
import xyz.nyist.vo.GroupAndFriendVO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author : fucong
 * @Date: 2021-03-12 13:42
 * @Description :
 */
@RestController
@RequestMapping("/friendGroup")
public class FriendGroupController {

    @Autowired
    private UserContext userContext;
    @Autowired
    private GroupService groupService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private MessageService messageService;

    @GetMapping("getGroupList")
    public Result<List<GroupAndFriendVO>> getGroupList() {
        return Result.success(userContext.getCurrentUser().getGroups()
                .stream().map(GroupAndFriendVO::forValue).collect(Collectors.toList())
        );
    }

    @PostMapping("addGroup")
    public Result<Void> addGroup(String groupName) {
        groupService.create(groupName);
        return Result.success();
    }

    @PostMapping("deleteGroup")
    public Result<Void> deleteGroup(Integer id) {
        groupService.delete(id);
        return Result.success();
    }

    @PostMapping("addFriend")
    public Result<Void> addFriend(@RequestBody Map<String, Object> data) {
        Boolean accept = (Boolean) data.get("accept");
        Integer messageId = (Integer) data.get("id");
        MessageEntity message = messageService.getById(messageId);
        message.setRead(true);
        if (accept) {
            String remark = (String) data.get("remark");
            Integer groupId = (Integer) data.get("group");
            String other = message.getOther();
            String[] split = other.split("-");
            friendService.create(message.getFrom(), message.getTo(), remark, split[0], Integer.valueOf(split[1]), groupId);
            message.setStatus(MessageStatus.ACCEPT);
        } else {
            message.setStatus(MessageStatus.REJECT);
        }
        messageService.update(message);
        return Result.success();
    }

    @PostMapping("deleteFriend")
    public Result<Void> deleteFriend(Integer id) {
        friendService.delete(id);
        return Result.success();
    }

    @PostMapping("updateFriend")
    public Result<Void> updateFriend(Integer id, Integer userId, String remark, Integer groupId) {
        friendService.update(id, userId, remark, groupId);
        return Result.success();
    }
}
