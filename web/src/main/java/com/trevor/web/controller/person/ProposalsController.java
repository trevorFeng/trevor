package com.trevor.web.controller.person;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ProposalContent;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:39
 **/
@Api(value = "用户异常举报及实名认证" ,description = "用户异常举报及实名认证")
@RestController
public class ProposalsController {

    /**
     * 上传文件
     * @param file
     * @return
     */
    @RequestMapping(value = "/api/proposals/file", method = {RequestMethod.POST}, produces = {MediaType.IMAGE_PNG_VALUE ,MediaType.IMAGE_JPEG_VALUE})
    public JsonEntity<String> uploadProposalsFile(@RequestParam("file") MultipartFile file){
        return null;
    }

    /**
     * 提交异常举报
     * @param proposalContent
     * @return
     */
    @RequestMapping(value = "/api/proposals/submit", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> submitProposals(@RequestBody ProposalContent proposalContent){
        return null;
    }

    /**
     * 实名认证
     * @param proposalContent
     * @return
     */
    @RequestMapping(value = "/api/proposals/auth", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> realNameAuth(@RequestBody ProposalContent proposalContent){
        return null;
    }

}
