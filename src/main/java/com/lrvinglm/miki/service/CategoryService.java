package com.lrvinglm.miki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lrvinglm.miki.aspect.LogAspect;
import com.lrvinglm.miki.domain.Category;
import com.lrvinglm.miki.domain.CategoryExample;
import com.lrvinglm.miki.mapper.CategoryMapper;
import com.lrvinglm.miki.req.CategoryQueryReq;
import com.lrvinglm.miki.req.CategorySaveReq;
import com.lrvinglm.miki.resp.CategoryQueryResp;
import com.lrvinglm.miki.resp.PageResp;
import com.lrvinglm.miki.utils.CopyUtil;
import com.lrvinglm.miki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;
    private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    public List<CategoryQueryResp> all(){
        //domain下的example mybaits自动生成了很多方法
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc ");
        //当作where语句
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);//查询到所有的Category实体
        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        return list;
    }

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){
        //domain下的example mybaits自动生成了很多方法
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc ");
        //当作where语句
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        PageHelper.startPage(req.getPage(),req.getSize());//只会分页最近的需要查询的sql，当页面多条sql时 把分页和sql放一起
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);//查询到所有的Category实体

        PageInfo<Category> pageInfo=new PageInfo<>(categoryList);
        LOG.info("页数:{}",pageInfo.getPages());
        LOG.info("行数：{}",pageInfo.getTotal());
//        List<CategoryResp> respList=new ArrayList<>();
        //遍历所有的Category属性给CategoryResp 并过滤掉不需要返回的属性
//        for (Category e:categoryList) {
//            CategoryResp categoryResp=new CategoryResp();
//            BeanUtils.copyProperties(e,categoryResp);
//            CategoryResp categoryResp = CopyUtil.copy(e, CategoryResp.class);
//            respList.add(categoryResp);
//        }
        List<CategoryQueryResp> respList = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> pageResp=new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /**
     * 保存
     */
    public void save(CategorySaveReq req){
        Category category=CopyUtil.copy(req,Category.class);
        if(ObjectUtils.isEmpty(category.getId())){
            //新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else{
            //更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    /**
     * 删除
     */
    public void delete(Long id){
        //删除指定id的数据
        categoryMapper.deleteByPrimaryKey(id);

    }
}
