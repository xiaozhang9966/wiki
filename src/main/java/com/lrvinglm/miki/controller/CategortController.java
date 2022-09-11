package com.lrvinglm.miki.controller;

import com.lrvinglm.miki.req.CategoryQueryReq;
import com.lrvinglm.miki.req.CategorySaveReq;
import com.lrvinglm.miki.resp.CommonResp;
import com.lrvinglm.miki.resp.CategoryQueryResp;
import com.lrvinglm.miki.resp.PageResp;
import com.lrvinglm.miki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/category")
public class CategortController {
    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")  //接口支持所有的请求方式
    public CommonResp list(@Valid CategoryQueryReq req){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list= categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        CommonResp resp = new CommonResp();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp();
        categoryService.delete(id);
        return resp;
    }
}
