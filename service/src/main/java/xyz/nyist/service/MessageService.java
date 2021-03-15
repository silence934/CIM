package xyz.nyist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import xyz.nyist.constant.MessageStatus;
import xyz.nyist.constant.MessageType;
import xyz.nyist.entity.MessageEntity;
import xyz.nyist.exception.CimException;
import xyz.nyist.repository.MessageRepository;
import xyz.nyist.result.ResultCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author : fucong
 * @Date: 2021-03-15 13:38
 * @Description :
 */
@Slf4j
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void create(MessageEntity messageEntity) {
        messageEntity.setStatus(MessageStatus.UN_READ);
        messageRepository.saveAndFlush(messageEntity);
    }


    public MessageEntity getById(Integer id) {
        return messageRepository.findById(id).orElseThrow(() -> new CimException(ResultCode.SYSTEM_RESOURCE_ERROR, "消息[id%d]不存在", id));
    }

    public Page<MessageEntity> getMessageByPage(Map<String, Integer> map) {
        Integer from = map.get("from");
        Integer to = map.get("to");
        int page = map.get("page") - 1;
        int size = map.get("size");
        Sort sort = Sort.by(Sort.Direction.DESC, "time");
        return messageRepository.findAllByPage(from, to, Arrays.asList(MessageType.LOGIN), PageRequest.of(page, size, sort));
    }

    public List<MessageEntity> getUnreadMessage(Integer userId) {
        return messageRepository.getUnReadMessage(userId);
    }

    public void update(MessageEntity message) {
        messageRepository.saveAndFlush(message);
    }
}
