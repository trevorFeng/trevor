package com.trevor.service.proposals;

import com.trevor.bo.JsonEntity;
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

}
