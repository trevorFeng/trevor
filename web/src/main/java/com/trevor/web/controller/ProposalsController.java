package com.trevor.web.controller;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ProposalContent;
import com.trevor.service.proposals.ProposalsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:39
 **/
@Api(value = "用户异常举报及实名认证" ,description = "用户异常举报及实名认证")
@RestController
public class ProposalsController {


    @Resource
    private ProposalsService proposalsService;

    @ApiOperation("上传文件")
    @RequestMapping(value = "/api/proposals/file", method = {RequestMethod.POST}, produces = {MediaType.IMAGE_PNG_VALUE ,MediaType.IMAGE_JPEG_VALUE})
    public JsonEntity<String> uploadProposalsFile(@RequestParam("file") MultipartFile file){
        return proposalsService.loadMaterial(file);
    }


    @ApiOperation("提交异常举报")
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
