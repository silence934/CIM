package xyz.nyist.event;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import xyz.nyist.configs.NettySocketIoConfig;
import xyz.nyist.entity.MessageEntity;

import java.util.List;


/**
 * @Author : fucong
 * @Date: 2021-02-02 10:22
 * @Description :
 */
@Slf4j
@Component
public class MessageEventListener implements ApplicationListener<MessageEvent> {

    @Override
    public void onApplicationEvent(MessageEvent event) {
        log.info("收到消息: " + event.getMessage());
        MessageEntity message = event.getMessage();
        Integer to = message.getTo();
        List<SocketIOClient> clients = NettySocketIoConfig.CLIENT_MAP.get(to);

        if (!CollectionUtils.isEmpty(clients)) {
            for (SocketIOClient client : clients) {
                client.sendEvent(event.getEventName().value(), message);
            }
        }
    }
}
