package xyz.nyist.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import xyz.nyist.constant.MessageStatus;
import xyz.nyist.constant.MessageType;
import xyz.nyist.entity.MessageEntity;
import xyz.nyist.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author: silence
 * @Date: 2021/5/6 22:22
 * @Description:
 */
@Data
@Builder
public class MessageVO {
    private Integer from;

    private Integer to;

    private UserVO fromUser;

    private UserVO toUser;

    private String data;

    private String other;

    private MessageType type;

    private MessageStatus status;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private Boolean isRead;

    public static MessageVO forValue(MessageEntity message){
        return Optional.ofNullable(message).map(m->MessageVO.builder()
                .from(m.getFrom())
                .to(m.getTo())
                .data(m.getData())
                .other(m.getOther())
                .type(m.getType())
                .status(m.getStatus())
                .time(m.getTime())
                .isRead(m.isRead())
                .build()).orElse(null);
    }

    public static MessageVO forValue(MessageEntity message, UserEntity from,UserEntity to){
        MessageVO messageVO = MessageVO.builder()
                .from(message.getFrom())
                .to(message.getTo())
                .data(message.getData())
                .other(message.getOther())
                .type(message.getType())
                .status(message.getStatus())
                .time(message.getTime())
                .isRead(message.isRead())
                .build();
        if (from!=null){
            messageVO.setFromUser(UserVO.forValue(from));
        }
        if (to!=null){
            messageVO.setFromUser(UserVO.forValue(to));
        }
        return messageVO;
    }
}
