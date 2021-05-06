package xyz.nyist.vo;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author: silence
 * @Date: 2021/5/5 15:58
 * @Description:
 */
@Data
@SuperBuilder
public class ChatSubjectVo {

    private Integer id;

    private String name;

    private String avatar;

}
