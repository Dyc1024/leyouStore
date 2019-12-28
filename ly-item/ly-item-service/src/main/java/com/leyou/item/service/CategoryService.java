package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.domain.Category;
import com.leyou.item.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper mapper;

    /**
     * 通过父id查询子分类
     * @param pid
     * @return
     */
    public List<Category> queryCategorysByParentId(Long pid) {
        //创建查询对象
        Category category = new Category();
        category.setParentId(pid);

        //获取查询结果
        List<Category> categories = mapper.select(category);

        //判空
        if(CollectionUtils.isEmpty(categories)){
            //为空 抛出自定义异常
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        //不为空
        return categories;
    }
    /**
     * 通过分类id集合查询分类名称集合
     * @param ids
     * @return
     */
    public List<String> findNamesByIds(List<Long> ids){
        List<Category> categories = mapper.selectByIdList(ids);
        return categories.stream().map(categorie -> categorie.getName()).collect(Collectors.toList());
    }

    public List<Category> queryCategorysByIds(List<Long> ids){
        List<Category> categories = mapper.selectByIdList(ids);

        //结果判断
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        //结果返回
        return categories;
    }

    public List<Category> queryByBrandId(Long bid) {
        return mapper.queryByBrandId(bid);
    }


    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    public Category findById(Long id) {
        return mapper.selectByPrimaryKey(id);
    }
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = mapper.selectByPrimaryKey(id);
        Category c2 = mapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = mapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }
}
