package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.domain.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService service;

    /**
     * 分页查询品牌
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false)String key
    ){
        return ResponseEntity.ok(service.queryBrandByPage(page,rows,sortBy,desc,key));
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids")List<Long> cids){
        //调用业务层
        service.saveBrand(brand,cids);

        //结果返回
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     *  品牌的修改
     * @param cids
     * @param brand
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        service.updateBrand(cids, brand);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * 品牌的删除
     * @param bid
     * @return
     */
    /**
     * 删除tb_brand中的数据,单个删除、多个删除二合一
     * @param bid
     * @return
     */
    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") String bid){
        String separator="-";
        if(bid.contains(separator)){
            String[] ids=bid.split(separator);
            for (String id:ids){
                service.deleteBrand(Long.parseLong(id));
            }
        }
        else {
            service.deleteBrand(Long.parseLong(bid));
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 通过目录id查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(service.queryBrandsByCid(cid));
    }

    /**
     * 通过id查询分类
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.queryBrandById(id));
    }

    /**
     * 通过id集合批量查询品牌
     * @param ids
     * @return
     */
    @GetMapping("/ids")
    public ResponseEntity<List<Brand>> queryBrandsByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> list = this.service.queryBrandsByIds(ids);
        if(list == null){
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
