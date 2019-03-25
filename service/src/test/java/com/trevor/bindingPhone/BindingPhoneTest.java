package com.trevor.bindingPhone;

import com.trevor.service.bindingPhone.BindingPhoneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 03/25/19 17:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BindingPhoneTest {

    @Resource
    private BindingPhoneService bindingPhoneService;

    @Test
    public void testBindingPhone(){
        Long userId = 1L;
        String phoneNum = "24567654567";
        bindingPhoneService.bindingPhone(userId ,phoneNum);
    }
}
