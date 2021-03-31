package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.nyist.dto.CrowdUpdateDTO;
import xyz.nyist.entity.CrowdEntity;
import xyz.nyist.entity.CrowdUserEntity;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.CrowdRepository;
import xyz.nyist.repository.CrowdUserRepository;
import xyz.nyist.result.ResultCode;

import java.util.Collection;
import java.util.List;

/**
 * @Author : fucong
 * @Date: 2021-03-16 10:31
 * @Description :
 */
@Slf4j
@Service
public class CrowdService {

    @Autowired
    private CrowdRepository crowdRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CrowdUserRepository crowdUserRepository;


    public void create(CrowdEntity crowdEntity) {
        crowdRepository.saveAndFlush(crowdEntity);
    }

    public void jsonCrowd(Integer userId, Integer crowdId) {
        CrowdEntity crowd = getById(crowdId);
        UserEntity user = userService.getById(userId);
        crowdUserRepository.saveAndFlush(CrowdUserEntity.builder()
                .user(user)
                .crowd(crowd)
                .build());
    }

    public CrowdEntity getById(Integer id) {
        return crowdRepository.findById(id).orElseThrow(() -> new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "群[id%d]不存在", id));
    }

    public List<CrowdEntity> getByIds(Collection<Integer> ids) {
        return crowdRepository.findAllById(ids);
    }

    public CrowdEntity findByName(String name) {
        return crowdRepository.findByName(name);
    }

    public void quitCrowd(Integer userId, Integer crowdId) {
        CrowdEntity crowd = getById(crowdId);
        if (crowd.getLordId().equals(userId)) {
            throw new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "群主无法退群");
        }
        UserEntity user = userService.getById(userId);

        crowdUserRepository.findByCrowdAndUser(crowd, user)
                .ifPresent(crowdUserEntity -> crowdUserRepository.delete(crowdUserEntity));
    }

    public void update(CrowdUpdateDTO crowdUpdate) {
        CrowdEntity entity = getById(crowdUpdate.getId());
        entity.setName(crowdUpdate.getName());
        entity.setAnnouncement(crowdUpdate.getAnnouncement());
        entity.setAvatar(crowdUpdate.getAvatar());
        crowdRepository.saveAndFlush(entity);
    }
}
