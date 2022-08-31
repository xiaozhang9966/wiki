package com.lrvinglm.miki.controller;

import com.lrvinglm.miki.domain.Ebook;
import com.lrvinglm.miki.service.EbookService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/ebook")
public class EbookControl {
    @Resource
    private EbookService ebookService;

    @RequestMapping("/list")  //接口支持所有的请求方式
    public List<Ebook> list(){
        return ebookService.list();
    }
}
