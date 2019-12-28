package com.leyou.item.controller;

import com.leyou.item.domain.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService service;

    /**
     * 根据父节点id查询分类
     *
     * @param pid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategorysByParentId(@RequestParam("pid") Long pid) {
        return ResponseEntity.ok(service.queryCategorysByParentId(pid));
    }

    /**
     * 根据id查询商品分类
     *
     * @param ids
     * @return
     */
    @GetMapping("/list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(service.queryCategorysByIds(ids));
    }

    /**
     * 通过品牌id查询商品分类
     *
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid) {
        List<Category> list = service.queryByBrandId(bid);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    @GetMapping("/id")
    public ResponseEntity<Category> findById(@RequestParam("id")Long id){
        Category category = service.findById(id);

        if(category == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category);
    }

    /**
     * 根据id集合查询分类名称集合
     *
     * @param ids
     * @return
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> findNamesByIds(@RequestParam("ids") List<Long> ids) {
        List<String> names = service.findNamesByIds(ids);

        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id){
        List<Category> list = this.service.queryAllByCid3(id);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
