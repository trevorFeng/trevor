package com.trevor.service.friendManager;

import com.google.common.collect.Lists;
import com.trevor.bo.FriendInfo;
import com.trevor.dao.FriendManageMapper;
import com.trevor.domain.FriendsManage;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private UserService userService;

    /**
     * 查询好友（申请通过和未通过的）
     * @return
     */
    @Override
    public List<FriendInfo> findRecevedCardRecord(User user) {
        List<FriendsManage> list =friendManageMapper.findByUserId(user.getId());
        List<Long> ids = list.stream().map(friendsManage -> friendsManage.getManageFriendId()).collect(Collectors.toList());
        Map<Long ,Integer> map = list.stream().collect(Collectors.toMap(FriendsManage::getManageFriendId ,FriendsManage::getAllowFlag));
        List<User> users = userService.findUsersByIds(ids);
        List<FriendInfo> friendInfos = Lists.newArrayList();
        users.forEach(user1 -> {
            FriendInfo friendInfo = new FriendInfo();
            friendInfo.setUserId(user.getId());
            friendInfo.setAppName(user.getAppName());
            friendInfo.setPictureUrl(user.getAppPictureUrl());
            friendInfo.setAllowFlag(map.get(user.getId()));
            friendInfos.add(friendInfo);
        });
        return friendInfos;
    }
}
