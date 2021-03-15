package xyz.nyist.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import xyz.nyist.exception.CimException;
import xyz.nyist.result.Result;
import xyz.nyist.result.ResultCode;
import xyz.nyist.utils.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author fucong
 * @date 2020/07/28 18:05
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(CimException.class)
    public Result<String> customHandler(CimException cimException) {
        log.error("业务异常， {}", cimException.getMessage());
        return Result.failed(cimException.getResultCode(), cimException.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> validationHandler(MethodArgumentNotValidException validationException) {
        log.error("参数验证异常，{}", validationException.getMessage());

        BindingResult bindingResult = validationException.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<ValidErrorMsg> errorMegs = new ArrayList<>();
        allErrors.forEach(objectError -> {
            ValidErrorMsg errorMsg = new ValidErrorMsg();
            FieldError fieldError = (FieldError) objectError;
            errorMsg.setFieldName(fieldError.getField());
            errorMsg.setObjectName(fieldError.getObjectName());
            errorMsg.setMessage(fieldError.getDefaultMessage());
            errorMegs.add(errorMsg);
        });

        return Result.failed(ResultCode.PARAM_ERROR, JsonUtil.obj2String(errorMegs));
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> validationHandler(ConstraintViolationException validationException) {
        log.debug("参数验证异常，{}", validationException.getMessage());
        Set<ConstraintViolation<?>> constraintViolations = validationException.getConstraintViolations();
        StringBuilder errorMsg = new StringBuilder();
        constraintViolations.forEach(constraintViolation -> {
            errorMsg.append(String.format("valid on class: %s message: %s, detail: %s", constraintViolation.getRootBeanClass().getName(), constraintViolation.getMessage(), JsonUtil.obj2String(constraintViolation)));
            errorMsg.append("\n");
        });
        return Result.failed(ResultCode.PARAM_ERROR, errorMsg.toString());
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> validationHandler(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.debug("参数验证异常，{}", missingServletRequestParameterException.getMessage());
        return Result.failed(ResultCode.PARAM_ERROR, missingServletRequestParameterException.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<String> validationHandler(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        log.debug("参数验证异常，{}", methodArgumentTypeMismatchException.getMessage());
        String argName = methodArgumentTypeMismatchException.getParameter().getParameterName();
        String errorMsg = String.format("argName: %s, message: %s", argName, methodArgumentTypeMismatchException.getMessage());
        return Result.failed(ResultCode.PARAM_ERROR, errorMsg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception exception) {
        log.error("未捕获的异常，{}", exception.getMessage());
        exception.printStackTrace();
        return Result.failed(ResultCode.SYSTEM_EXECUTION_ERROR, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> permissionHandler(HttpServletRequest req, AccessDeniedException accessDeniedException) {
        log.error("权限不足  访问路径: " + req.getRequestURI());
        String message = accessDeniedException.getMessage();
        return Result.failed(message);
    }


    @Data
    public static class ValidErrorMsg {
        private String objectName;
        private String fieldName;
        private String message;
    }

}
