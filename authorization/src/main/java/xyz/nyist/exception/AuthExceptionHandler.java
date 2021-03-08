package xyz.nyist.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.nyist.result.Result;
import xyz.nyist.result.ResultCode;

/**
 * @author fucong
 */
@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    /**
     * 用户名和密码异常
     */
    @ExceptionHandler({InvalidGrantException.class})
    public Result<ResultCode> handleInvalidGrantException(InvalidGrantException e) {
        return Result.failed(ResultCode.USERNAME_OR_PASSWORD_ERROR);
    }


    /**
     * 账户异常(禁用、锁定、过期)
     */
    @ExceptionHandler({InternalAuthenticationServiceException.class})
    public Result<String> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        e.printStackTrace();
        return Result.failed(e.getMessage());
    }

    /**
     * 其他异常
     */
    @ExceptionHandler({Exception.class})
    public Result<String> handleException(Exception e) {
        e.printStackTrace();
        return Result.failed(e.getMessage());
    }

}
