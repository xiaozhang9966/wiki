package com.jiawa.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.wiki.aspect.LogAspect;
import com.jiawa.wiki.domain.Content;
import com.jiawa.wiki.domain.Doc;
import com.jiawa.wiki.domain.DocExample;
import com.jiawa.wiki.mapper.ContentMapper;
import com.jiawa.wiki.mapper.DocMapper;
import com.jiawa.wiki.req.DocQueryReq;
import com.jiawa.wiki.req.DocSaveReq;
import com.jiawa.wiki.resp.DocQueryResp;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.utils.CopyUtil;
import com.jiawa.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class DocService {
    @Resource
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;
    private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    public List<DocQueryResp> all(Long ebookId){
        //domain下的example mybaits自动生成了很多方法
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc ");
        //当作where语句
        List<Doc> docList = docMapper.selectByExample(docExample);//查询到所有的Doc实体
        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req){
        //domain下的example mybaits自动生成了很多方法
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc ");
        //当作where语句
        DocExample.Criteria criteria = docExample.createCriteria();
        PageHelper.startPage(req.getPage(),req.getSize());//只会分页最近的需要查询的sql，当页面多条sql时 把分页和sql放一起
        List<Doc> docList = docMapper.selectByExample(docExample);//查询到所有的Doc实体

        PageInfo<Doc> pageInfo=new PageInfo<>(docList);
        LOG.info("页数:{}",pageInfo.getPages());
        LOG.info("行数：{}",pageInfo.getTotal());
//        List<DocResp> respList=new ArrayList<>();
        //遍历所有的Doc属性给DocResp 并过滤掉不需要返回的属性
//        for (Doc e:docList) {
//            DocResp docResp=new DocResp();
//            BeanUtils.copyProperties(e,docResp);
//            DocResp docResp = CopyUtil.copy(e, DocResp.class);
//            respList.add(docResp);
//        }
        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp=new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /**
     * 保存
     */
    public void save(DocSaveReq req){
        Doc doc=CopyUtil.copy(req,Doc.class);
        Content content=CopyUtil.copy(req,Content.class);
        if(ObjectUtils.isEmpty(doc.getId())){
            //新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        }else{
            //更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0){
                contentMapper.insert(content);
            }
        }
    }

    /**
     * 删除
     */
    public void delete(Long id) {
        //删除指定id的数据
        docMapper.deleteByPrimaryKey(id);
    }
    public void delete(List<String> ids){

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc ");
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);

    }
      public String findContent(Long id){    //查找文档内容，如果找不到会进行一个判断
        //删除指定id的数据
            Content content = contentMapper.selectByPrimaryKey(id);
            if (ObjectUtils.isEmpty(content)) {
                return "";
            } else {
                return content.getContent();
            }

    }
}
