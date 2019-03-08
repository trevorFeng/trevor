package com.trevor.bo;

import com.trevor.common.MessageCode;

/**
 * @author trevor
 * @date 2019/3/7 13:33
 */
public class ResponseHelper {

    public ResponseHelper() {
    }

    public static <T> JsonEntity<T> createInstance(T object , MessageCode messageCode) {
        JsonEntity<T> response = new JsonEntity(object);
        response.setCode(messageCode.getCode());
        response.setMessage(messageCode.getMessage());
        return response;
    }

    public static <T> JsonEntity<T> createErrorInstance(MessageCode messageCode) {
        JsonEntity<T> response = new JsonEntity();
        response.setCode(messageCode.getCode());
        response.setMessage(messageCode.getMessage());
        return response;
    }
}

