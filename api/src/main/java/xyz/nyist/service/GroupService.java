package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.nyist.component.UserContext;
import xyz.nyist.entity.GroupEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.GroupRepository;
import xyz.nyist.result.ResultCode;

/**
 * @Author : fucong
 * @Date: 2021-03-12 14:06
 * @Description :
 */
@Slf4j
@Service
public class GroupService {

    @Autowired
    private UserContext userContext;
    @Autowired
    private GroupRepository groupRepository;

    public GroupEntity getById(Integer id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "用户组[id:%s]不存在", id));
    }

    public void create(String groupName) {
        if (userContext.getCurrentUser().getGroups().stream().anyMatch(g -> g.getName().equals(groupName))) {
            throw new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "用户组已[]存在", groupName);
        }
        groupRepository.saveAndFlush(GroupEntity.builder()
                .name(groupName)
                .user(userContext.getCurrentUser())
                .build());
    }

    public void delete(Integer id) {
        groupRepository.deleteById(id);
    }

    public void update(Integer id, String groupName) {
        GroupEntity groupEntity = getById(id);
        groupEntity.setName(groupName);

        groupRepository.saveAndFlush(groupEntity);
    }


}
