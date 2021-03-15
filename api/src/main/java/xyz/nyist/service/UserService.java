package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.UserRepository;

import java.util.Collection;
import java.util.List;

/**
 * @Author : fucong
 * @Date: 2021-03-12 14:46
 * @Description :
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new CimException("用户账号[%s]不存在", username));
    }

    public UserEntity getById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new CimException("用户id[%d]不存在", id));
    }

    public List<UserEntity> getByIds(Collection<Integer> ids) {
        return userRepository.findAllById(ids);
    }

    public UserEntity findByUsernameOrNickname(String key) {
        return userRepository.findByUsernameOrNickname(key, key).stream().findFirst().orElse(null);
    }
}
