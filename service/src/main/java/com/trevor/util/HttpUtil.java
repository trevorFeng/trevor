package com.trevor.util;

import com.alibaba.fastjson.JSON;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author trevor
 * @date 2019/3/4 12:16
 */
public class HttpUtil {

    /**
     * 发送get请求，并将返回的json字符串转化为对象
     * @param url 请求的url
     * @param clazz json字符串对应的Class
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T httpGet(String url ,Class<T> clazz) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        String responseString = response.body().string();
        T t = JSON.parseObject(responseString, clazz);
        return t;

    }
}
