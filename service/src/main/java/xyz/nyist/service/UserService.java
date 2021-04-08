package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.nyist.component.UserContext;
import xyz.nyist.dto.UserRegisterDTO;
import xyz.nyist.entity.GroupEntity;
import xyz.nyist.entity.RoleEntity;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.entity.UserSecurityEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.RoleRepository;
import xyz.nyist.repository.UserRepository;
import xyz.nyist.result.ResultCode;

import java.util.*;

/**
 * @Author : fucong
 * @Date: 2021-03-12 14:46
 * @Description :
 */
@Slf4j
@Service
public class UserService implements UserDetailsService {

    private static final List<String> DEFAULT_AVATAR_LIST =
            Arrays.asList("https://dss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1603365312,3218205429&fm=26&gp=0.jpg",
                    "https://dss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1603365312,3218205429&fm=26&gp=0.jpg");

    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserContext userContext;


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


    public void registerUser(UserRegisterDTO userRegister) {

        if (userRepository.findByUsername(userRegister.getUsername()).isPresent()) {
            throw new CimException("账号[%s]已存在", userRegister.getUsername());
        }

        Random random = new Random();
        int n = random.nextInt(DEFAULT_AVATAR_LIST.size());

        List<RoleEntity> roles = roleRepository.findAllByDisplayName("普通角色");

        UserEntity userEntity = UserEntity.builder()
                .avatar(DEFAULT_AVATAR_LIST.get(n))
                .username(userRegister.getUsername())
                .password(PASSWORD_ENCODER.encode(userRegister.getPassword()))
                .phone(userRegister.getPhone())
                .mail(userRegister.getMail())
                .enabled(true)
                .roles(roles)
                .sex("男")
                .build();

        List<UserSecurityEntity> securityEntities = Collections.singletonList(UserSecurityEntity.builder()
                .question(userRegister.getQuestion())
                .user(userEntity)
                .answer(userRegister.getAnswer()).build());

        List<GroupEntity> groupEntities = Collections.singletonList(GroupEntity.builder()
                .user(userEntity)
                .name("我的好友")
                .build());

        userEntity.setSecurities(securityEntities);
        userEntity.setGroups(groupEntities);

        userRepository.saveAndFlush(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("账号不存在"));
    }

   
    public void updatePwd(String pwd, String npwd) {
        UserEntity user = userContext.getCurrentUser();
        if (!PASSWORD_ENCODER.matches(pwd, user.getPassword())) {
            throw new CimException(ResultCode.USERNAME_OR_PASSWORD_ERROR, "用户名或密码错误");
        }
        user.setPassword(PASSWORD_ENCODER.encode(npwd));
        userRepository.saveAndFlush(user);
    }
}
