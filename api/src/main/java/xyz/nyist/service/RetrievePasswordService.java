package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.nyist.constant.RedisKey;
import xyz.nyist.dto.RetrievePasswordDTO;
import xyz.nyist.dto.VerificationCodeDTO;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.event.MailEvent;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.UserRepository;
import xyz.nyist.utils.CimUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author : fucong
 * @Date: 2021-03-30 15:15
 * @Description :
 */
@Slf4j
@Service
public class RetrievePasswordService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BusProperties busProperties;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    public void getCode(VerificationCodeDTO verificationCode) {
        UserEntity user = userService.getByUsername(verificationCode.getUsername());
        if (!Objects.equals(user.getMail(), verificationCode.getMail())) {
            throw new CimException("账号与邮箱不匹配");
        }

        Long expire = redisTemplate.getExpire(RedisKey.CODE_KEY + user.getId(), TimeUnit.MINUTES);

        if (expire != null && expire < 2) {
            String code = CimUtil.getRandomString(6);

            redisTemplate.opsForValue().set(RedisKey.CODE_KEY + user.getId(), code, 3L, TimeUnit.MINUTES);

            MailEvent mailEvent = new MailEvent(this, busProperties.getId());
            mailEvent.setMail(user.getMail());
            mailEvent.setContent(code);
            mailEvent.setSubject("找回密码");


            applicationContext.publishEvent(mailEvent);
        }

    }

    public void retrievePassword(RetrievePasswordDTO retrievePasswordDTO) {
        UserEntity user = userService.getByUsername(retrievePasswordDTO.getUsername());
        String code = redisTemplate.opsForValue().get(RedisKey.CODE_KEY + user.getId());
        if (Objects.equals(code, retrievePasswordDTO.getCode())) {
            redisTemplate.delete(RedisKey.CODE_KEY + user.getId());
            user.setPassword(PASSWORD_ENCODER.encode(retrievePasswordDTO.getPassword()));
            userRepository.saveAndFlush(user);
        } else {
            throw new CimException("验证码无效");
        }
    }
}
