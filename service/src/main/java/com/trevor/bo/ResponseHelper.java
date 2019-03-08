package com.trevor.bo;

import com.trevor.common.MessageCodeEnum;

/**
 * @author trevor
 * @date 2019/3/7 13:33
 */
public class ResponseHelper {

    public ResponseHelper() {
    }

    public static <T> JsonEntity<T> createInstance(T object , MessageCodeEnum messageCodeEnum) {
        JsonEntity<T> response = new JsonEntity(object);
        response.setCode(messageCodeEnum.getCode());
        response.setMessage(messageCodeEnum.getMessage());
        return response;
    }

    public static <T> JsonEntity<T> withErrorInstance(MessageCodeEnum messageCodeEnum) {
        JsonEntity<T> response = new JsonEntity();
        response.setCode(messageCodeEnum.getCode());
        response.setMessage(messageCodeEnum.getMessage());
        return response;
    }
}

