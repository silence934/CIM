package xyz.nyist.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import xyz.nyist.constant.EventType;
import xyz.nyist.entity.Message;

/**
 * @Author : fucong
 * @Date: 2021-02-02 10:21
 * @Description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageEvent extends RemoteApplicationEvent {

    private Message message;

    private EventType eventType;


    public MessageEvent(Object source, String originService, Message message) {
        super(source, originService, "*");
        this.message = message;
    }
}
