package com.trevor.web.controller.niuniu;

import com.trevor.bo.JsonEntity;
import com.trevor.service.niuniu.bo.NiuniuRoomParameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author trevor
 * @date 2019/3/7 12:16
 */
@RestController
public class NiuniuController {

    /**
     * 创建一个房间
     * @param niuniuRoomParameter 房间参数
     * @return 房间的唯一id
     */
    @RequestMapping(value = "/api/niuniu/createRoom", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> createRoom(@RequestBody NiuniuRoomParameter niuniuRoomParameter){
        return null;
    }

}
