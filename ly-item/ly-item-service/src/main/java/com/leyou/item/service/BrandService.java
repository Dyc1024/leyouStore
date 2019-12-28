package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.domain.Brand;
import com.leyou.item.mapper.BrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper mapper;

    /**
     * 分页查询品牌
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page,rows);

        //创建过滤条件
        Example example = new Example(Brand.class);

        //过滤
        if(StringUtils.isNotBlank(key)){
            //添加过滤条件
            example.createCriteria().orLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }

        //排序
        if(StringUtils.isNotBlank(sortBy)){
            //拼接查询条件
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }

        //查询
        List<Brand> brands = mapper.selectByExample(example);

        //判空
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //解析分页结果
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        //正常返回
        return new PageResult<Brand>(pageInfo.getTotal(),brands);
    }

    /**
     * 新增商品
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //初始化品牌id
        brand.setId(null);

        //保存品牌
        int count = mapper.insert(brand);

        //判断是否成功
        if(count != 1){
            throw new LyException(ExceptionEnum.SAVE_BRAND_ERROR);
        }

        //遍历目录id
        for (Long cid : cids) {
            //向中间表插入数据
            count = mapper.insertCategoryBrand(cid, brand.getId());

            //判断是否成功
            if(count != 1){
                throw new LyException(ExceptionEnum.SAVE_BRAND_ERROR);
            }
        }
    }

    public Brand queryBrandById(Long id){
        Brand brand = mapper.selectByPrimaryKey(id);

        //结果判断
        if(brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //正常返回
        return brand;
    }
    /**
     * 品牌的修改
     *
     * @param cids
     * @param brand
     */
    @Transactional
    public void updateBrand(List<Long> cids, Brand brand) {
        //修改品牌
        mapper.updateByPrimaryKeySelective(brand);
        //维护中间表
        for (Long cid : cids) {
            mapper.updateCategoryBrand(cid, brand.getId());
        }

    }

    /**
     * 品牌的删除后
     * @param bid
     */
    @Transactional
    public void deleteBrand(Long bid) {
        //删除品牌表
        mapper.deleteByPrimaryKey(bid);
        //维护中间表
        mapper.deleteCategoryBrandByBid(bid);
    }

    public List<Brand> queryBrandsByCid(Long cid) {
        List<Brand> brands = mapper.queryBrandsByCid(cid);

        //结果判断
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //正常返回
        return brands;
    }

    public List<Brand> queryBrandsByIds(List<Long> ids) {
        List<Brand> brandList = mapper.selectByIdList(ids);

        //结果判断
        if(CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        return brandList;
    }
}
