package com.trevor.service.bindingPhone;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.enums.MessageCodeEnum;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 03/22/19 13:15
 */
@Service
public class BindingPhoneServiceImpl implements BindingPhoneService{

    @Resource
    private UserService userService;

    /**
     * 绑定手机号
     * @param userId
     * @param phoneNum
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<String> bindingPhone(Long userId, String phoneNum) {
        User user = new User();
        user.setUserId(userId);
        userService.updatePhoneByUserId(userId ,phoneNum);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.BINDING_SUCCESS);
    }
}
