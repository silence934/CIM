package xyz.nyist.event;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import xyz.nyist.configs.NettySocketIoConfig;
import xyz.nyist.entity.Message;


/**
 * @Author : fucong
 * @Date: 2021-02-02 10:22
 * @Description :
 */
@Component
public class MessageEventListener implements ApplicationListener<MessageEvent> {

    @Override
    public void onApplicationEvent(MessageEvent event) {
        System.out.println("收到消息: " + event.getMessage());

        Message message = event.getMessage();
        String from = message.getFrom();
        for (SocketIOClient client : NettySocketIoConfig.CLIENT_MAP.get(from)) {
            client.sendEvent("messageevent", message);
        }
    }
}
