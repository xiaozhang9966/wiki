package com.lrvinglm.miki.service;


import com.lrvinglm.miki.domain.Ebook;
import com.lrvinglm.miki.domain.EbookExample;
import com.lrvinglm.miki.mapper.EbookMapper;
import com.lrvinglm.miki.req.EbookReq;
import com.lrvinglm.miki.resp.EbookResp;
import com.lrvinglm.miki.utils.CopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        //domain下的example mybaits自动生成了很多方法
        EbookExample ebookExample = new EbookExample();
        //当作where语句
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){//不为空才执行
            criteria.andNameLike("%"+req.getName()+"%"); //模糊查询的条件
        }

        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);//查询到所有的Ebook实体

//        List<EbookResp> respList=new ArrayList<>();
        //遍历所有的Ebook属性给EbookResp 并过滤掉不需要返回的属性
//        for (Ebook e:ebookList) {
//            EbookResp ebookResp=new EbookResp();
//            BeanUtils.copyProperties(e,ebookResp);
//            EbookResp ebookResp = CopyUtil.copy(e, EbookResp.class);
//            respList.add(ebookResp);
//        }
        List<EbookResp> respList = CopyUtil.copyList(ebookList, EbookResp.class);
        return respList;
    }
}
