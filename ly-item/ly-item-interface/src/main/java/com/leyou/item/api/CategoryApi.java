package com.leyou.item.api;

import com.leyou.item.domain.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {
    /**
     * 根据父节点id查询分类
     * @param pid
     * @return
     */
    @GetMapping("/category/list")
    public List<Category> queryCategorysByParentId(@RequestParam("pid")Long pid);
    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    @GetMapping("/id")
    public Category findById(@RequestParam("id")Long id);

    /**
     * 根据id批量查询分类
     * @param ids
     * @return
     */
    @GetMapping("/category/list/ids")
    List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);


    /**
     * 根据id集合查询分类名称集合
     * @param ids
     * @return
     */
    @GetMapping("/category/names")
    public List<String> findNamesByIds(@RequestParam("ids")List<Long> ids);
}

