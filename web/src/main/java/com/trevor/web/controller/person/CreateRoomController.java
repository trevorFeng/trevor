package com.trevor.web.controller.person;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.WebSessionUser;
import com.trevor.service.UserService;
import com.trevor.service.createRoom.CreateRoomService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.util.SessionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author trevor
 * @date 2019/3/8 16:51
 */
@Api(value = "创建房间" ,description = "创建房间接口")
@RestController
public class CreateRoomController {

    @Resource
    private CreateRoomService createRoomService;

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest request;

    /**
     * 创建一个房间
     * @param niuniuRoomParameter 房间参数
     * @return 房间的唯一id
     */
    @ApiOperation("创建一个房间")
    @RequestMapping(value = "/api/room/create/niuniu", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Long> createRoom(@RequestBody NiuniuRoomParameter niuniuRoomParameter){
        WebSessionUser webSessionUser = userService.getUserByCookie(request);
        return createRoomService.createRoom(niuniuRoomParameter ,webSessionUser);
    }
}
