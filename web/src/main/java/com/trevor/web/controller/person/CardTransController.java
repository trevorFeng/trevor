package com.trevor.web.controller.person;

import com.trevor.bo.JsonEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author trevor
 * @date 2019/3/8 16:50
 */
@RestController
public class CardTransController {

    @RequestMapping(value = "/api/cardTrans/create/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> createCardPackage(@RequestBody Integer cardNum ){
       return null;
    }
}
