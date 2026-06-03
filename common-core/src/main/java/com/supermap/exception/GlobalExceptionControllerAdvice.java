package com.supermap.exception;

import com.supermap.common.enumeration.BizCodeEnum;
import com.supermap.common.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gzw
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.supermap.modules"})
public class GlobalExceptionControllerAdvice {

    /**
     * 处理参数校验异常
     *
     * @param e {@link MethodArgumentNotValidException MethodArgumentNotValidException}
     * @return R
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Map<String, String>> handleValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        result.getFieldErrors().forEach((item) -> errorMap.put(item.getField(), item.getDefaultMessage()));
        if (log.isDebugEnabled())
            log.error("Validation failed for argument{}", errorMap);
        return R.error(BizCodeEnum.VALID_PARAM_EXCEPTION, errorMap);
    }

    /**
     * 处理未知异常
     *
     * @param e {@link DataIntegrityViolationException dataIntegrityViolationException}
     * @return R
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public R<Throwable> handleException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return R.error(BizCodeEnum.DUPLICATE_KEY_EXCEPTION.getCode(), e.getLocalizedMessage());
    }

    /**
     * 处理未知异常
     *
     * @param throwable {@link Throwable Throwable}
     * @return R
     */
    @ExceptionHandler(Throwable.class)
    public R<Throwable> handleException(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), throwable.getLocalizedMessage());
    }

}
