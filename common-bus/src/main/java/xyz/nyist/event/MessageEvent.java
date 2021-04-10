package xyz.nyist.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import xyz.nyist.constant.EventName;
import xyz.nyist.entity.MessageEntity;

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

    private MessageEntity message;

    private EventName eventName;


    public MessageEvent(Object source, String originService, MessageEntity message) {
        super(source, originService, "cim-socket");
        this.message = message;
    }
}
