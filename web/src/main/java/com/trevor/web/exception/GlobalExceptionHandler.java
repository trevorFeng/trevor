package com.trevor.web.exception;

import com.trevor.bo.JsonEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author trevor
 * @date 2019/3/4 12:47
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public JsonEntity<Object> handleOtherExceptions(Exception e) {

        return new JsonEntity<Object>();
    }
}
