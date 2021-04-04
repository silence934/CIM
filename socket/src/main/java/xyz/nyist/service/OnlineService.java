package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xyz.nyist.constant.EventName;
import xyz.nyist.constant.MessageType;
import xyz.nyist.constant.RedisKey;
import xyz.nyist.entity.MessageEntity;
import xyz.nyist.event.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author : fucong
 * @Date: 2021-04-02 16:35
 * @Description :
 */
@Slf4j
@Service
public class OnlineService {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BusProperties busProperties;

    private static final MessageEntity STATISTICS_ONLINE_MESSAGE = MessageEntity.builder()
            .type(MessageType.STATISTICS_ONLINE).build();

    public void join(Integer userId) {
        redissonClient.getAtomicLong(RedisKey.USER_ONLINE_KEY + userId).incrementAndGet();
        MessageEvent messageEvent = new MessageEvent(this, busProperties.getId(), STATISTICS_ONLINE_MESSAGE);
        messageEvent.setEventName(EventName.SOCKET_MESSAGE);
        applicationContext.publishEvent(messageEvent);
    }


    public void leave(Integer userId) {
        redissonClient.getAtomicLong(RedisKey.USER_ONLINE_KEY + userId).decrementAndGet();
        MessageEvent messageEvent = new MessageEvent(this, busProperties.getId(), STATISTICS_ONLINE_MESSAGE);
        messageEvent.setEventName(EventName.SOCKET_MESSAGE);
        applicationContext.publishEvent(messageEvent);
    }

    public List<Integer> getOnlineUser() {
        Set<String> keys = redisTemplate.keys(RedisKey.USER_ONLINE_KEY + "*");
        if (keys == null) {
            return new ArrayList<>();
        }

        return keys.stream().filter(k -> redissonClient.getAtomicLong(k).get() > 0)
                .map(i -> Integer.valueOf(i.substring(RedisKey.USER_ONLINE_KEY.length())))
                .collect(Collectors.toList());
    }

}
