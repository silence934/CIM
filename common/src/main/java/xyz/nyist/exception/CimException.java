package xyz.nyist.exception;

/**
 * @Author : fucong
 * @Date: 2021-03-07 18:03
 * @Description :
 */
public class CimException extends RuntimeException {

    public CimException(String format, Object... arguments) {
        super(String.format(format, arguments));
    }

}
