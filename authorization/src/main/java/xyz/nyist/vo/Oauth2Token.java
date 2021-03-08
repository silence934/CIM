package xyz.nyist.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author fucong
 */
@Data
@Builder
public class Oauth2Token {

    private String token;

    private String refreshToken;

    private int expiresIn;

}
