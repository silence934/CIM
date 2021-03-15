package xyz.nyist.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import xyz.nyist.constant.MessageStatus;
import xyz.nyist.constant.MessageType;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Author : silence
 * @Date: 2021-01-22 11:24
 * @Description :
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "message")
public class MessageEntity extends BaseEntity {

    @Column(name = "`from`")
    private Integer from;

    @Column(name = "`to`")
    private Integer to;

    @Column(name = "`data`", columnDefinition = "TEXT")
    private String data;

    @Column(name = "`other`", columnDefinition = "TEXT")
    private String other;

    @Column(name = "`type`")
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(name = "`status`")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;


    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "`time`", length = 19)
    private LocalDateTime time;


    @Column(name = "is_read", columnDefinition = "TINYINT(1) default 0", nullable = false)
    private boolean isRead;

}
