package com.trevor.service.proposals;

import com.trevor.bo.Authentication;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ProposalContent;
import org.springframework.web.multipart.MultipartFile;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-23 13:46
 **/

public interface ProposalsService {

    /**
     * 上传文件
     * @param multipartFile
     * @return
     */
    JsonEntity<String> loadMaterial(MultipartFile multipartFile);

    /**
     * 提交异常举报
     * @param proposalContent
     * @return
     */
    JsonEntity<Object> submitProposals(ProposalContent proposalContent ,Long userId);

    /**
     * 实名认证
     * @param authentication
     * @return
     */
    JsonEntity<Object> realNameAuth(Authentication authentication ,Long userId);

}
