package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import xyz.nyist.component.UserContext;
import xyz.nyist.constant.MessageStatus;
import xyz.nyist.constant.MessageType;
import xyz.nyist.dto.TagMessageDTO;
import xyz.nyist.entity.CrowdEntity;
import xyz.nyist.entity.MessageEntity;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.CrowdUserRepository;
import xyz.nyist.repository.MessageRepository;
import xyz.nyist.result.ResultCode;
import xyz.nyist.vo.MessageVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author : fucong
 * @Date: 2021-03-15 13:38
 * @Description :
 */
@Slf4j
@Service
public class MessageService {

    @Autowired
    private UserContext userContext;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CrowdUserRepository crowdUserRepository;
    @Autowired
    private CrowdService crowdService;

    private static final Sort TIME_DESC_SORT = Sort.by(Sort.Direction.DESC, "time");


    public void create(MessageEntity messageEntity) {
        messageEntity.setStatus(MessageStatus.UN_READ);
        messageRepository.saveAndFlush(messageEntity);
    }

    public MessageEntity getById(Integer id) {
        return messageRepository.findById(id).orElseThrow(() -> new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "消息[id%d]不存在", id));
    }

    public Page<MessageVO> getMessageByPage(Map<String, Integer> map) {
        Integer from = map.get("from");
        Integer to = map.get("to");
        Integer type = map.get("type");
        int page = map.get("page") - 1;
        int size = map.get("size");

        Page<MessageEntity> messageEntityPage;
        if (from > 9999 || to > 9999) {
            //群消息
            messageEntityPage = messageRepository.findAllByTo(
                    Math.max(from, to),
                    Arrays.asList(MessageType.LOGIN, MessageType.VIDEO, MessageType.ADD_FRIEND),
                    PageRequest.of(page, size, TIME_DESC_SORT));

            return messageEntityPage.map(MessageVO::forValue);
        } else if (Objects.equals(type, 1)) {
            //验证消息

            messageEntityPage = messageRepository.findAllByPageAndTypeIn(
                    from,
                    Collections.singletonList(MessageType.ADD_FRIEND),
                    PageRequest.of(page, size, TIME_DESC_SORT));

            List<Integer> ids = messageEntityPage.getContent().stream().map(MessageEntity::getFrom)
                    .collect(Collectors.toList());

            Map<Integer, UserEntity> userMap = new HashMap<>(ids.size() + 1, 1);
            userService.getByIds(ids).forEach(u -> {
                userMap.put(u.getId(), u);
            });

            return messageEntityPage.map(msg -> MessageVO.forValue(msg, userMap.get(msg.getFrom()), null));
        } else {

            messageEntityPage = messageRepository.findAllByPageAndTypeNotIn(
                    from,
                    to,
                    Arrays.asList(MessageType.LOGIN, MessageType.VIDEO, MessageType.ADD_FRIEND),
                    PageRequest.of(page, size, TIME_DESC_SORT));

            return messageEntityPage.map(MessageVO::forValue);
        }
    }

    public List<MessageEntity> getUnreadMessage() {
        List<MessageEntity> unReadMessage = messageRepository.getUnReadMessageWithFriend(userContext.getCurrentUser().getId());
        userContext.getCurrentUser().getCrowdUsers().forEach(c -> {
            List<MessageEntity> list = messageRepository.getUnReadMessageWithCrowd(c.getCrowd().getId(), c.getUpTo());
            unReadMessage.addAll(list);
        });
        return unReadMessage.stream().sorted(Comparator.comparing(MessageEntity::getTime)).collect(Collectors.toList());
    }

    public void update(MessageEntity message) {
        messageRepository.saveAndFlush(message);
    }

    public void tagMessage(TagMessageDTO tagMessage) {
        Integer from = tagMessage.getFrom();
        Integer to = tagMessage.getTo();
        if (from > 9999 || to > 9999) {
            //群消息
            CrowdEntity crowd = crowdService.getById(Math.max(from, to));
            UserEntity user = userService.getById(Math.min(from, to));
            crowdUserRepository.findByCrowdAndUser(crowd, user)
                    .ifPresent(crowdUserEntity -> {
                        crowdUserEntity.setUpTo(tagMessage.getTime());
                        crowdUserRepository.saveAndFlush(crowdUserEntity);
                    });
        } else if (tagMessage.getType().equals(MessageType.ADD_FRIEND)) {
            //验证消息
            List<MessageEntity> messages = messageRepository.findVerifyMessageByTime(from, tagMessage.getTime());
            messages.forEach(m -> m.setRead(true));
            messageRepository.saveAll(messages);
        } else {
            //好友消息
            List<MessageEntity> messages = messageRepository.findAllByTime(from, to, tagMessage.getTime());
            messages.forEach(m -> m.setRead(true));
            messageRepository.saveAll(messages);
        }

    }
}
