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
import xyz.nyist.entity.Message;
import xyz.nyist.event.MessageEvent;

import java.util.ArrayList;
import java.util.List;

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

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String mac = client.getHandshakeData().getSingleUrlParam("mac");

        List<SocketIOClient> clients = NettySocketIoConfig.CLIENT_MAP.computeIfAbsent(mac, k -> new ArrayList<>());
        clients.add(client);

        client.sendEvent("messageevent", "嗨," + mac + " 你好! 咱们已建立连接");
        log.info("客户端:" + client.getSessionId() + "已连接,mac=" + mac);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String mac = client.getHandshakeData().getSingleUrlParam("mac");
        List<SocketIOClient> clients = NettySocketIoConfig.CLIENT_MAP.get(mac);
        clients.remove(client);
        log.info("客户端:" + client.getSessionId() + "断开连接");
    }

    @OnEvent(value = "sendEvent")
    public void receiveMessage(Message message) {

    }


    private void send(Message message) {
        String from = message.getFrom();

        for (SocketIOClient client : NettySocketIoConfig.CLIENT_MAP.get(from)) {
            client.sendEvent("messageevent", message);
        }

        MessageEvent messageEvent = new MessageEvent(this, busProperties.getId(), message);
        applicationContext.publishEvent(messageEvent);
    }

}
