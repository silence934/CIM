package xyz.nyist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nyist.component.UserContext;
import xyz.nyist.entity.MessageEntity;
import xyz.nyist.result.Result;
import xyz.nyist.service.MessageService;

import java.util.List;

/**
 * @Author : fucong
 * @Date: 2021-03-14 11:47
 * @Description :
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private UserContext userContext;
    @Autowired
    private MessageService messageService;

    /**
     * 获取未读消息
     *
     * @return 未读消息
     */
    @GetMapping("/getUnreadMessage")
    public Result<List<MessageEntity>> getUnreadMessage() {
        return Result.success(messageService.getUnreadMessage(userContext.getCurrentUser().getId()));
    }


}
