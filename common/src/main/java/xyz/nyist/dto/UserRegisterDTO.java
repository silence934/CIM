package xyz.nyist.dto;

import lombok.Data;

/**
 * @Author : fucong
 * @Date: 2021-03-29 15:32
 * @Description :
 */
@Data
public class UserRegisterDTO {

    private String username;

    private String password;

    private String phone;

    private String mail;

    private String sex;

    private String question;

    private String answer;

}
