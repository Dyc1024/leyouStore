package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.domain.SpecGroup;
import com.leyou.item.domain.SpecParam;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecGroupsByCid(Long cid) {
        //创建条件对象
        SpecGroup sg = new SpecGroup();

        //设置条件对象属性
        sg.setCid(cid);

        //使用条件对象查询结果
        List<SpecGroup> specGroups = specGroupMapper.select(sg);

        //判空
        if(CollectionUtils.isEmpty(specGroups)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }

        //结果返回
        return specGroups;
    }

    /**
     * 保存规格分组
     * @param specGroup
     */
    public void saveSpecGroup(SpecGroup specGroup){
        int i = specGroupMapper.insert(specGroup);

        //结果判断
        if(i != 1){
            throw new LyException(ExceptionEnum.SAVE_SPEC_GROUP_ERROR);
        }
    }

    /**
     * 修改规格分组
     * @param specGroup
     */
    public void updateSpecGroup(SpecGroup specGroup) {
        int i = specGroupMapper.updateByPrimaryKey(specGroup);

        //结果判断
        if(i != 1){
            throw new LyException(ExceptionEnum.UPDATE_SPEC_GROUP_ERROR);
        }
    }

    /**
     * 通过分组id查询分组参数
     * @param gid
     * @return
     */
    public List<SpecParam> querySpecParamsByGid(Long gid) {
        //创建查询对象条件
        SpecParam specParam = new SpecParam();

        //设置条件属性
        specParam.setGroupId(gid);

        //获取查询结果
        List<SpecParam> specParams = specParamMapper.select(specParam);

        //判空
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        //结果返回
        return specParams;
    }

    /**
     * 根据规格分组id删除规格分组
     * @param gid
     */
    public void deleteSpecGroup(Long gid) {
        int i = specGroupMapper.deleteByPrimaryKey(gid);

        //结果判断
        if(i != 1){
            throw new LyException(ExceptionEnum.DELETE_SPEC_GROUP_ERROR);
        }
    }

    /**
     * 新增规格参数
     * @param specParam
     */
    public void saveSpecParam(SpecParam specParam) {
        int i = specParamMapper.insert(specParam);

        //结果判断
        if(i != 1){
            throw new LyException(ExceptionEnum.SAVE_SPEC_PARAM_ERROR);
        }
    }

    /**
     * 修改规格参数
     * @param specParam
     */
    public void updateSpecParam(SpecParam specParam) {
        int i = specParamMapper.updateByPrimaryKeySelective(specParam);

        System.out.println(i);

        //结果判断
        if(i != 1){
            throw new LyException(ExceptionEnum.UPDATE_SPEC_PARAM_ERROR);
        }
    }

    /**
     * 删除规格参数
     * @param pid
     */
    public void deleteSpecParam(Long pid) {
        int i = specParamMapper.deleteByPrimaryKey(pid);

        //结果判断
        if(i != 1){
            throw new LyException(ExceptionEnum.DELETE_SPEC_PARAM_ERROR);
        }
    }

    public List<SpecParam> querySpecParamList(Long gid, Long cid, Boolean searching) {
        //创建查询对象条件
        SpecParam specParam = new SpecParam();

        //设置条件属性
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);

        //获取查询结果
        List<SpecParam> specParams = specParamMapper.select(specParam);

        //判空
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        //结果返回
        return specParams;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        //查询规格组
        List<SpecGroup> specGroups = querySpecGroupsByCid(cid);

        //查询当前分类下的所有参数
        List<SpecParam> specParams = querySpecParamList(null, cid, null);

        //初始化Map
        Map<Long,List<SpecParam>> map = new HashMap<>();

        //遍历参数集合
        for (SpecParam specParam : specParams) {
            //第一次添加
            if(!map.containsKey(specParam.getGroupId())){
                map.put(specParam.getGroupId(),new ArrayList<SpecParam>());
            }

            //添加数据到map
            map.get(specParam.getGroupId()).add(specParam);
        }

        //属性封装
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }

        return specGroups;
    }



    public List<SpecGroup> querySpecsByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> groups = this.querySpecGroupsByCid(cid);
        SpecParam param = new SpecParam();
        groups.forEach(g -> {
            // 查询组内参数
            g.setParams(this.querySpecParamsByGid(g.getId()));
        });
        return groups;
    }

    /**
     * 根据分类id查询该分类下的规格参数组及组下的参数
     * @param cid
     * @return
     */
    public List<SpecGroup> findSpecGroupWithParamByCid(Long cid) {
        SpecGroup instance = new SpecGroup();
        instance.setCid(cid);

        List<SpecGroup> groupList = specGroupMapper.select(instance);

        groupList.forEach(group -> {
            SpecParam param = new SpecParam();
            param.setGroupId(group.getId());
            List<SpecParam> specParams = specParamMapper.select(param);
            group.setParams(specParams);
        });

        return groupList;
    }
}
