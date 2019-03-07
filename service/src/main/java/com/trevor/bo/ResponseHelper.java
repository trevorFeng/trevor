package com.trevor.bo;

/**
 * @author trevor
 * @date 2019/3/7 13:33
 */
public class ResponseHelper {

    public ResponseHelper() {
    }

    public static <T> JsonEntity<T> insertSuccess(T object) {
        JsonEntity<T> response = new JsonEntity(object);
        response.setCode(100);
        response.setMessage("创建成功");
        return response;
    }
}

