package com.trevor.web.controller.person;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.WebSessionUser;
import com.trevor.domain.FriendsManage;
import com.trevor.service.friendManager.FriendManagerService;
import com.trevor.util.SessionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
    private FriendManagerService findRecevedCardRecord;

    /**
     * 查询好友（申请通过和未通过的）
     * @return
     */
    @ApiOperation(value = "查询好友（申请通过和未通过的）")
    @RequestMapping(value = "/api/friend/manager/query", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<FriendsManage>> findRecevedCardRecord(){
        WebSessionUser webSessionUser = SessionUtil.getSessionUser();
        return null;
    }

}
