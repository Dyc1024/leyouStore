package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.domain.Sku;
import com.leyou.item.domain.Spu;
import com.leyou.item.domain.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    /**
     * 根据spuid查询spudetail
     * @param id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("id") Long id);

    /**
     * 通过spuid查询sku
     * @param id
     * @return
     */
    @GetMapping("/sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    /**
     * 分页查询spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    public PageResult<Spu> querySpusByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "key",required = false)String key
    );
    /**
     * 分页查询Spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    public PageResult<SpuBo> findSpuBoByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    /*
     **购物车查询sku
     */
    @GetMapping("sku/{id}")
    public Sku querySkuById(@PathVariable("id")Long id);

}