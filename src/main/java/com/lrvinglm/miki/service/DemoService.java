package com.lrvinglm.miki.service;


import com.lrvinglm.miki.domain.Demo;
import com.lrvinglm.miki.mapper.DemoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DemoService {
    @Resource
    private DemoMapper demoMapper;

    public List<Demo> list(){
        return demoMapper.selectByExample(null);
    }
}
