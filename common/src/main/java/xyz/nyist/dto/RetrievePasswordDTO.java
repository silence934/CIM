package xyz.nyist.dto;

import lombok.Data;

/**
 * @Author : fucong
 * @Date: 2021-03-31 22:17
 * @Description :
 */
@Data
public class RetrievePasswordDTO {
    private String username;

    private String password;

    private String code;
}
