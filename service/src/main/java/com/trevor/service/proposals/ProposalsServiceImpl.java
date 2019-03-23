package com.trevor.service.proposals;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.common.MessageCodeEnum;
import com.trevor.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
}
