package xyz.nyist.dto;

import lombok.Data;

/**
 * @Author : fucong
 * @Date: 2021-04-08 13:41
 * @Description :
 */
@Data
public class UserUpdateDTO {

    private Integer id;

    private String nickname;

    private String avatar;

    private String phone;

    private String mail;

    private String sex;
}
