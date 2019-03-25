package com.trevor.service.proposals;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.Authentication;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ProposalContent;
import com.trevor.bo.ResponseHelper;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.UserProposalsMapper;
import com.trevor.domain.UserProposals;
import com.trevor.service.user.UserService;
import com.trevor.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-23 13:49
 **/
@Service
@Slf4j
public class ProposalsServiceImpl implements ProposalsService{

    @Value("${file.path}")
    private String filepath;

    @Resource
    private UserProposalsMapper userProposalsMapper;

    @Resource
    private UserService userService;

    /**
     * 上传文件
     * @param multipartFile
     * @return
     */
    @Override
    public JsonEntity<String> loadMaterial(MultipartFile multipartFile) {
        if (multipartFile.isEmpty() || multipartFile == null) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.HANDLER_FAILED);
        }
        String newFileName = UUID.randomUUID().toString()+System.currentTimeMillis();
        Boolean saveFile = null;
        try {
            saveFile = FileUtil.saveFileToDirectory(this.filepath ,newFileName ,multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("保存文件错误");
            return ResponseHelper.withErrorInstance(MessageCodeEnum.HANDLER_FAILED);
        }
        if (saveFile) {
            return ResponseHelper.createInstance(newFileName ,MessageCodeEnum.HANDLER_SUCCESS);
        }
        return ResponseHelper.withErrorInstance(MessageCodeEnum.HANDLER_FAILED);
    }

    /**
     * 提交异常举报
     * @param proposalContent
     * @return
     */
    @Override
    public JsonEntity<Object> submitProposals(ProposalContent proposalContent ,Long userId) {
        UserProposals userProposals = new UserProposals();
        userProposals.setUserId(userId);
        userProposals.setMessage(proposalContent.getContent());
        userProposals.setFileUrls(JSON.toJSONString(proposalContent.getFileUrls()));
        userProposalsMapper.insertOne(userProposals);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.HANDLER_SUCCESS);
    }

    /**
     * 实名认证
     * @param authentication
     * @return
     */
    @Override
    public JsonEntity<Object> realNameAuth(Authentication authentication, Long userId) {
        userService.realNameAuth(userId ,authentication);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.HANDLER_SUCCESS);
    }
}
