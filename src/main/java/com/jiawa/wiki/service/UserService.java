package com.jiawa.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.wiki.aspect.LogAspect;
import com.jiawa.wiki.domain.User;
import com.jiawa.wiki.domain.UserExample;
import com.jiawa.wiki.exception.BusinessException;
import com.jiawa.wiki.exception.BusinessExceptionCode;
import com.jiawa.wiki.mapper.UserMapper;
import com.jiawa.wiki.req.UserQueryReq;
import com.jiawa.wiki.req.UserSaveReq;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.resp.UserQueryResp;
import com.jiawa.wiki.utils.CopyUtil;
import com.jiawa.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;
    private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    public PageResp<UserQueryResp> list(UserQueryReq req){



        //domain下的example mybaits自动生成了很多方法
        UserExample userExample = new UserExample();
        //当作where语句
        UserExample.Criteria criteria = userExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getLoginName())){//不为空才执行
            criteria.andLoginNameEqualTo(req.getLoginName()); //模糊查询的条件
        }

        PageHelper.startPage(req.getPage(),req.getSize());//只会分页最近的需要查询的sql，当页面多条sql时 把分页和sql放一起
        List<User> userList = userMapper.selectByExample(userExample);//查询到所有的User实体

        PageInfo<User> pageInfo=new PageInfo<>(userList);
        LOG.info("页数:{}",pageInfo.getPages());
        LOG.info("行数：{}",pageInfo.getTotal());
//        List<UserResp> respList=new ArrayList<>();
        //遍历所有的User属性给UserResp 并过滤掉不需要返回的属性
//        for (User e:userList) {
//            UserResp userResp=new UserResp();
//            BeanUtils.copyProperties(e,userResp);
//            UserResp userResp = CopyUtil.copy(e, UserResp.class);
//            respList.add(userResp);
//        }
        List<UserQueryResp> respList = CopyUtil.copyList(userList, UserQueryResp.class);
        PageResp<UserQueryResp> pageResp=new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /**
     * 保存
     */
    public void save(UserSaveReq req){
        User user=CopyUtil.copy(req,User.class);
        if(ObjectUtils.isEmpty(user.getId())){
            User userDB = selectByLoginName(req.getLoginName());
            if (ObjectUtils.isEmpty(userDB)) {
            //新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                //用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }else{
            //更新
            userMapper.updateByPrimaryKey(user);
        }
    }

    /**
     * 删除
     */
    public void delete(Long id){
        //删除指定id的数据
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String LoginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName); //模糊查询的条件
        List<User> userList = userMapper.selectByExample(userExample);//查询到所有的User实体
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        } else {
            return userList.get(0);
        }
    }
}
