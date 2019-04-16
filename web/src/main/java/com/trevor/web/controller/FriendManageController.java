package com.trevor.web.controller;

import com.trevor.bo.FriendInfo;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.common.MessageCodeEnum;
import com.trevor.domain.FriendsManage;
import com.trevor.domain.User;
import com.trevor.service.friendManager.FriendManagerService;
import com.trevor.service.user.UserService;
import com.trevor.util.ThreadLocalUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 一句话描述该类作用:【好友管理】
 *
 * @author: trevor
 * @create: 2019-03-03 23:05
 **/
@Api(value = "好友管理" ,description = "好友管理相关接口")
@RestController
public class FriendManageController {

    @Resource
    private FriendManagerService friendManagerService;

    /**
     * 查询好友（申请通过和未通过的）
     * @return
     */
    @ApiOperation(value = "查询好友（申请通过和未通过的）")
    @RequestMapping(value = "/api/friend/manager/query", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<FriendInfo>> findRecevedCardRecord(){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        List<FriendInfo> friendInfos = friendManagerService.findRecevedCardRecord(user);
        ThreadLocalUtil.getInstance().remove();
        return ResponseHelper.createInstance(friendInfos , MessageCodeEnum.QUERY_SUCCESS);
    }

    /**
     * 申请成为房主的好友
     * @return
     */
    @ApiOperation(value = "申请成为房主的好友")
    @RequestMapping(value = "/api/friend/manager/query/{roomId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> applyRoomAuth(@PathVariable("roomId") Long roomId){
        return null;
    }

    /**
     * 踢出好友
     * @return
     */
    @ApiOperation(value = "踢出好友")
    @RequestMapping(value = "/api/friend/manager/remove/{userId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> removeRoomAuth(@PathVariable("userId") Long userId){
        return null;
    }

    /**
     * 通过好友申请
     * @return
     */
    @ApiOperation(value = "通过好友申请")
    @RequestMapping(value = "/api/friend/manager/pass/{userId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> passRoomAuth(@PathVariable("userId") Long userId){
        return null;
    }


}
