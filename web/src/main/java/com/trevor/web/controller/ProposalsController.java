package com.trevor.web.controller;

import com.trevor.bo.Authentication;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ProposalContent;
import com.trevor.bo.WebSessionUser;
import com.trevor.service.proposals.ProposalsService;
import com.trevor.service.user.UserService;
import com.trevor.util.CookieUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:39
 **/
@Api(value = "用户异常举报及实名认证" ,description = "用户异常举报及实名认证")
@RestController
@Validated
public class ProposalsController {


    @Resource
    private ProposalsService proposalsService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @ApiOperation("上传文件")
    @RequestMapping(value = "/api/proposals/file", method = {RequestMethod.POST}, produces = {MediaType.IMAGE_PNG_VALUE ,MediaType.IMAGE_JPEG_VALUE})
    public JsonEntity<String> uploadProposalsFile(@RequestParam("file") MultipartFile file){
        return proposalsService.loadMaterial(file);
    }


    @ApiOperation("提交异常举报")
    @RequestMapping(value = "/api/proposals/submit", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> submitProposals(@RequestBody @Validated ProposalContent proposalContent){
        String opendi = CookieUtils.getOpenid(request);
        WebSessionUser webSessionUser = userService.getWebSessionUserByOpneid(opendi);
        JsonEntity<Object> objectJsonEntity = proposalsService.submitProposals(proposalContent, webSessionUser.getId());
        return objectJsonEntity;
    }

    /**
     * 实名认证
     * @param authentication
     * @return
     */
    @RequestMapping(value = "/api/proposals/auth", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> realNameAuth(@RequestBody Authentication authentication){
        String opendi = CookieUtils.getOpenid(request);
        WebSessionUser webSessionUser = userService.getWebSessionUserByOpneid(opendi);
        return proposalsService.realNameAuth(authentication ,webSessionUser.getId());
    }

}
