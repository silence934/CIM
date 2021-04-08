package xyz.nyist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import xyz.nyist.dto.UserUpdateDTO;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.event.ArtifactDeleteEvent;
import xyz.nyist.event.ArtifactUseEvent;
import xyz.nyist.repository.UserRepository;

/**
 * @Author : fucong
 * @Date: 2021-04-08 14:23
 * @Description :
 */
@Service
public class ApiUserService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BusProperties busProperties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    public void update(UserUpdateDTO userUpdate) {
        UserEntity user = userService.getById(userUpdate.getId());
        String oldAvatar = user.getAvatar();

        if (!oldAvatar.equals(userUpdate.getAvatar())) {
            ArtifactDeleteEvent deleteEvent = new ArtifactDeleteEvent(this, busProperties.getId(), oldAvatar);
            ArtifactUseEvent useEvent = new ArtifactUseEvent(this, busProperties.getId(), userUpdate.getAvatar());

            applicationContext.publishEvent(deleteEvent);
            applicationContext.publishEvent(useEvent);
        }

        user.setSex(userUpdate.getSex());
        user.setAvatar(userUpdate.getAvatar());
        user.setPhone(userUpdate.getPhone());
        user.setMail(userUpdate.getMail());
        user.setNickname(userUpdate.getNickname());
        userRepository.saveAndFlush(user);
    }
}
