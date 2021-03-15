package xyz.nyist.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import xyz.nyist.configs.NettySocketIoConfig;
import xyz.nyist.constant.EventName;
import xyz.nyist.constant.MessageType;
import xyz.nyist.entity.MessageEntity;
import xyz.nyist.event.MessageEvent;
import xyz.nyist.service.MessageService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author : silence
 * @Date: 2020-11-12 10:06
 * @Description :
 */
@Slf4j
@Component
public class NettySocketEvent {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BusProperties busProperties;
    @Autowired
    private MessageService messageService;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("客户端:" + client.getSessionId() + "已连接");
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        Collection<List<SocketIOClient>> col = NettySocketIoConfig.CLIENT_MAP.values();
        for (List<SocketIOClient> sockets : col) {
            while (sockets.contains(client)) {
                sockets.remove(client);
            }
        }
        log.info("客户端:" + client.getSessionId() + "断开连接");
    }

    @OnEvent(value = "loginEvent")
    public void loginEvent(SocketIOClient client, MessageEntity message) {
        if (message.getType() != MessageType.LOGIN) {
            return;
        }
        List<SocketIOClient> clients = NettySocketIoConfig.CLIENT_MAP
                .computeIfAbsent(message.getFrom(), k -> new ArrayList<>());
        clients.add(client);
    }

    @OnEvent(value = "logoutEvent")
    public void logoutEvent(SocketIOClient client, MessageEntity message) {
        if (message.getType() != MessageType.LOGOUT) {
            return;
        }
        List<SocketIOClient> clients = NettySocketIoConfig.CLIENT_MAP.get(message.getFrom());
        clients.remove(client);
        log.info("用户(id):" + message.getFrom() + "下线");
    }


    @OnEvent(value = "sendEvent")
    public void receiveMessage(MessageEntity message) {
        messageService.create(message);
        send(message);
    }

    @OnEvent(value = "selectChat")
    public void selectChat(SocketIOClient client, Map<String, Integer> map) {
        client.sendEvent(EventName.GET_CHAT.value(), messageService.getMessageByPage(map));
    }

    private void send(MessageEntity message) {
        MessageEvent messageEvent = new MessageEvent(this, busProperties.getId(), message);
        messageEvent.setEventName(EventName.SOCKET_MESSAGE);
        applicationContext.publishEvent(messageEvent);
    }

}
