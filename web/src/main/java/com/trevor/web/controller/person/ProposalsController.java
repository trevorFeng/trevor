package com.trevor.web.controller.person;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ProposalContent;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:39
 **/
@Controller
public class ProposalsController {

    @RequestMapping(value = "/api/proposals/file", method = {RequestMethod.POST}, produces = {MediaType.IMAGE_PNG_VALUE ,MediaType.IMAGE_JPEG_VALUE})
    public JsonEntity<String> uploadProposalsFile(@RequestParam("file") MultipartFile files , String content){
        return null;
    }

    @RequestMapping(value = "/api/proposals/submit", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> submitProposals(@RequestBody ProposalContent proposalContent){
        return null;
    }
}
