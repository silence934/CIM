package xyz.nyist.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import xyz.nyist.bo.CimAuthentication;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.UserRepository;

/**
 * @Author : fucong
 * @Date: 2021-03-12 13:47
 * @Description :
 */
@Component
public class UserContext {

    @Autowired
    private UserRepository userRepository;

    public UserEntity getCurrentUser() {
        CimAuthentication cimAuthentication = (CimAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findById(cimAuthentication.getId()).orElseThrow(() -> new CimException("用户id[%s]不存在", cimAuthentication.getId()));
    }
}
