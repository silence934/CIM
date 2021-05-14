package xyz.nyist.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import xyz.nyist.constant.MessageType;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * @Author : fucong
 * @Date: 2021-03-21 23:13
 * @Description :
 */
@Data
public class TagMessageDTO {

    Integer from;

    Integer to;
    
    MessageType type;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "`time`", length = 19)
    private LocalDateTime time;
}
