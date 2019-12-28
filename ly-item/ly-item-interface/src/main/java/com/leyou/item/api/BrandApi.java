package com.leyou.item.api;

import com.leyou.item.domain.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BrandApi {
    /**
     * 通过id查询分类
     * @param id
     * @return
     */
    @GetMapping("/brand/{id}")
    Brand queryBrandById(@PathVariable("id") Long id);

    /**
     * 通过id集合批量查询品牌
     * @param ids
     * @return
     */
    @GetMapping("/brand/ids")
    List<Brand> queryBrandsByIds(@RequestParam("ids") List<Long> ids);


}
