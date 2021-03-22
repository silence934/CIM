package xyz.nyist.event;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import xyz.nyist.configs.NettySocketIoConfig;
import xyz.nyist.entity.CrowdEntity;
import xyz.nyist.entity.MessageEntity;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.service.CrowdService;

import java.util.List;


/**
 * @Author : fucong
 * @Date: 2021-02-02 10:22
 * @Description :
 */
@Slf4j
@Component
public class MessageEventListener implements ApplicationListener<MessageEvent> {

    @Autowired
    private CrowdService crowdService;

    @Override
    public void onApplicationEvent(MessageEvent event) {
        log.info("收到消息: " + event.getMessage());
        MessageEntity message = event.getMessage();
        Integer to = message.getTo();
        if (to > 9999) {
            CrowdEntity crowd = crowdService.getById(to);
            for (UserEntity user : crowd.getUsers()) {
                if (!user.getId().equals(message.getFrom())) {
                    List<SocketIOClient> clients = NettySocketIoConfig.CLIENT_MAP.get(user.getId());

                    if (!CollectionUtils.isEmpty(clients)) {
                        for (SocketIOClient client : clients) {
                            client.sendEvent(event.getEventName().value(), message);
                        }
                    }
                }
            }

        } else {
            List<SocketIOClient> clients = NettySocketIoConfig.CLIENT_MAP.get(to);

            if (!CollectionUtils.isEmpty(clients)) {
                for (SocketIOClient client : clients) {
                    client.sendEvent(event.getEventName().value(), message);
                }
            }
        }
    }
}
