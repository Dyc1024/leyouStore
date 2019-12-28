package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.domain.Sku;
import com.leyou.item.domain.Spu;
import com.leyou.item.domain.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpusByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "key",required = false)String key
    ){
        return ResponseEntity.ok(goodsService.querySpusByPage(page,rows,saleable,key));
    }

    /**
     * 新增商品
     * @param spu
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        goodsService.saveGoods(spu);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品
     * @param spu
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 删除商品
     * @param id
     * @return
     */
    @DeleteMapping("/goods/{id}")
    public ResponseEntity<Void> deleteGoods(@PathVariable("id") Long id){
        goodsService.deleteGoods(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    /**
     * 上下架商品
     * @param id
     *
     */
    @PutMapping("/goods/updown/{id}")
    public ResponseEntity<Void> updowmGoods(@PathVariable("id") Long id){
         goodsService.updownGoods(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    /**
     * 根据spuid查询spudetail
     * @param id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.querySpuDetailBySpuId(id));
    }

    /**
     * 通过spuid查询sku
     * @param id
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        return ResponseEntity.ok(goodsService.querySkuBySpuId(id));
    }

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.querySpuBySpuId(id));
    }
    /*
    **购物车查询sku
     */
    @GetMapping("sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id")Long id){
        Sku sku = this.goodsService.querySkuById(id);
        if (sku == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(sku);
    }
}
