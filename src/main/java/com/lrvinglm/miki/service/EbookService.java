package com.lrvinglm.miki.service;


import com.lrvinglm.miki.domain.Ebook;
import com.lrvinglm.miki.domain.EbookExample;
import com.lrvinglm.miki.mapper.EbookMapper;
import com.lrvinglm.miki.req.EbookReq;
import com.lrvinglm.miki.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        criteria.andNameLike("%"+req.getName()+"%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> respList=new ArrayList<>();
        for (Ebook e:ebookList) {
            EbookResp ebookResp=new EbookResp();
            BeanUtils.copyProperties(e,ebookResp);
            respList.add(ebookResp);
        }
        return respList;
    }
}
