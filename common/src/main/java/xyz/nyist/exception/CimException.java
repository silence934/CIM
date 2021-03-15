package xyz.nyist.exception;

import xyz.nyist.result.ResultCode;

/**
 * @Author : fucong
 * @Date: 2021-03-07 18:03
 * @Description :
 */
public class CimException extends RuntimeException {

    private final ResultCode resultCode;

    public CimException(String format, Object... arguments) {
        this(ResultCode.SYSTEM_EXECUTION_ERROR, format, arguments);
    }

    public CimException(ResultCode resultCode, String format, Object... arguments) {
        super(String.format(format, arguments));
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
