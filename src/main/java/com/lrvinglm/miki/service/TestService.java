package com.lrvinglm.miki.service;


import com.lrvinglm.miki.domain.Test;
import com.lrvinglm.miki.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestService {
    @Resource
    private TestMapper testMapper;

    public List<Test> list(){
        return testMapper.list();
    }
}
