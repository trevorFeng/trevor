package com.trevor.service.friendManager;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.UserInfo;
import com.trevor.dao.FriendManageMapper;
import com.trevor.domain.FriendsManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-03 23:56
 **/
@Slf4j
@Service
public class FriendManagerServiceImpl implements FriendManagerService {

    @Resource
    private FriendManageMapper friendManageMapper;

    /**
     * 查询好友（申请通过和未通过的）
     * @param userInfo
     * @return
     */
    @Override
    public JsonEntity<List<FriendsManage>> findRecevedCardRecord(UserInfo userInfo) {

        return null;
    }
}
