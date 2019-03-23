package com.trevor.service.bindingPhone;


import com.trevor.bo.JsonEntity;

public interface BindingPhoneService {

    /**
     * 绑定手机号
     * @param userId
     * @param phoneNum
     * @return
     */
    JsonEntity<String> bindingPhone(Long userId ,String phoneNum);
}
