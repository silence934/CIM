package xyz.nyist.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * @Author : fucong
 * @Date: 2021-03-30 13:42
 * @Description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MailEvent extends RemoteApplicationEvent {

    private String mail;

    private String content;

    private String subject;

    private boolean isHtml;


    public MailEvent(Object source, String originService) {
        super(source, originService, "*");
    }
}
