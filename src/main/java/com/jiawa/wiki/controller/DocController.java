package com.jiawa.wiki.controller;

import com.jiawa.wiki.req.DocQueryReq;
import com.jiawa.wiki.req.DocSaveReq;
import com.jiawa.wiki.resp.DocQueryResp;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/doc")
public class DocController {
    @Resource
    private DocService docService;

    @GetMapping("/list")  //接口支持所有的请求方式
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list= docService.list(req);
        resp.setContent(list);
        return resp;
    }
    @GetMapping("/all")  //接口支持所有的请求方式
    public CommonResp all(){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list= docService.all();
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp();
        docService.delete(id);
        return resp;
    }
}
