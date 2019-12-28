package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.domain.*;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);
    /**
     * 分页查询spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return`
     */
    public PageResult<Spu> querySpusByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);

        //创建条件对象
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //添加模糊搜索条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }

        //添加上下架过滤
        if(saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }

        //添加默认排序
        example.setOrderByClause("last_update_time DESC");

        //获取查询结果
        List<Spu> spus = spuMapper.selectByExample(example);

        //判断
        if(CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }

        //解析分类和品牌名称
        loadCategoryAndBrandName(spus);

        //解析页面结果
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);

        //结果返回
        return new PageResult<>(spuPageInfo.getTotal(),spus);
    }

    /**
     * 加载spu的分类名和品牌名
     * @param spus
     */
    private void loadCategoryAndBrandName(List<Spu> spus) {
        //遍历集合
        for (Spu spu : spus) {
            //查询分类
            List<String> cnames = categoryService.queryCategorysByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());

            //封装分类名称
            spu.setCname(StringUtils.join(cnames,"/"));

            //查询品牌
            Brand brand = brandService.queryBrandById(spu.getBrandId());

            //封装品牌名称
            spu.setBname(brand.getName());
        }
    }

    /**
     * 新增商品
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        //完善spu信息
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);

        //新增spu
        int count = spuMapper.insert(spu);

        //结果判断
        if(count != 1){
            throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
        }

        //获取spu_detail
        SpuDetail spuDetail = spu.getSpuDetail();

        //完善数据
        spuDetail.setSpuId(spu.getId());

        //新增spu_detail
        count = spuDetailMapper.insert(spuDetail);

        //结果判断
        if(count != 1){
            throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
        }

        //获取sku集合
        List<Sku> skus = spu.getSkus();

        //创建库存对象集合
        ArrayList<Stock> stocks = new ArrayList<>();

        //遍历集合
        for (Sku sku : skus) {
            //完善数据
            sku.setId(null);
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            sku.setSpuId(spu.getId());

            //新增sku
            count = skuMapper.insert(sku);

            //结果判断
            if(count != 1){
                throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
            }

            //创建库存对象
            Stock stock = new Stock();

            //数据完善
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            //新增到库存对象结合
            stocks.add(stock);
        }

        //批量新增库存
        count = stockMapper.insertList(stocks);

        //结果判断
        if(count != stocks.size()){
            throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
        }
        sendMessage(spu.getId(),"insert");
    }

    /**
     * 修改商品
     * @param spu
     */
    @Transactional
    public void updateGoods(Spu spu) {
        if(spu.getId() == null){
            throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
        }

        //创建sku对象
        Sku sku = new Sku();

        //参数设置
        sku.setSpuId(spu.getId());

        //查询sku
        List<Sku> skuList = skuMapper.select(sku);

        //结果判断
        if(!CollectionUtils.isEmpty(skuList)){
            //删除sku
            skuMapper.delete(sku);

            //删除stock
            List<Long> skuIds = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(skuIds);
        }

        //设置spu属性
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(null);

        //修改spu
        int count = spuMapper.updateByPrimaryKeySelective(spu);

        //结果判断
        if(count != 1){
            throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
        }

        //修改detail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

        //结果判断
        if(count != 1){
            throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
        }

        //获取sku集合
        List<Sku> skus = spu.getSkus();

        //创建库存对象集合
        ArrayList<Stock> stocks = new ArrayList<>();

        //遍历集合
        for (Sku sku1 : skus) {
            //完善数据
            sku1.setId(null);
            sku1.setCreateTime(new Date());
            sku1.setLastUpdateTime(new Date());
            sku1.setSpuId(spu.getId());

            //新增sku
            count = skuMapper.insert(sku1);

            //结果判断
            if(count != 1){
                throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
            }

            //创建库存对象
            Stock stock = new Stock();

            //数据完善
            stock.setSkuId(sku1.getId());
            stock.setStock(sku1.getStock());

            //新增到库存对象结合
            stocks.add(stock);
        }

        //批量新增库存
        count = stockMapper.insertList(stocks);

        //结果判断
        if(count != stocks.size()){
            throw new LyException(ExceptionEnum.SAVE_GOODS_ERROR);
        }

        sendMessage(spu.getId(),"update");

    }
    /**
     * 删除商品
     * @param id
     */
    @Transactional
    public void deleteGoods(long id) {
        //删除spu表中的数据
        this.spuMapper.deleteByPrimaryKey(id);

        //删除spu_detail中的数据
        Example example = new Example(SpuDetail.class);
        example.createCriteria().andEqualTo("spuId", id);
        this.spuDetailMapper.deleteByExample(example);


        List<Sku> skuList = this.skuMapper.selectByExample(example);
        for (Sku sku : skuList) {
            //删除sku中的数据
            this.skuMapper.deleteByPrimaryKey(sku.getId());
            //删除stock中的数据
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        }
        sendMessage(id,"delete");
    }
    /**
     * 上下架商品
     * @param id
     */
    @Transactional
    public void updownGoods(Long id){
        Spu spu =spuMapper.selectByPrimaryKey(id);
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId",id);
        List<Sku> skuList = skuMapper.selectByExample(example);
        if(spu.getSaleable()) {
            spu.setSaleable(false);
            spuMapper.updateByPrimaryKeySelective(spu);
            for (Sku sku : skuList){
                sku.setEnable(false);
                skuMapper.updateByPrimaryKeySelective(sku);
            }
        }else{
            spu.setSaleable(true);
            spuMapper.updateByPrimaryKeySelective(spu);
            for (Sku sku:skuList){
                sku.setEnable(true);
                skuMapper.updateByPrimaryKeySelective(sku);
            }
        }

    }
    /**
     * 根据spuid查询spudetail
     * @param id
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);

        //结果判断
        if(spuDetail == null){
            throw new LyException(ExceptionEnum.SPUDETAIL_NOT_FOUND);
        }

        //正常返回
        return spuDetail;
    }

    /**
     * 通过spuid查询sku
     * @param id
     * @return
     */
    public List<Sku> querySkuBySpuId(Long id) {
        //创建条件对象
        Sku sku = new Sku();

        //设置条件属性
        sku.setSpuId(id);

        //获取查询结果
        List<Sku> skus = skuMapper.select(sku);

        //结果判断
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }

        //遍历集合
        for (Sku sku1 : skus) {
            //通过skuid查询库存
            Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());

            //结果判断
            if(stock == null){
                throw new LyException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
            }

            //完善sku数据
            sku1.setStock(stock.getStock());
        }

        //正常返回
        return skus;

    }

    public Spu querySpuBySpuId(Long id) {
        //获取查询结果
        Spu spu = spuMapper.selectByPrimaryKey(id);

        //结果判断
        if(spu == null){
            //没有结果
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }

        //查询detail
        spu.setSpuDetail(querySpuDetailBySpuId(spu.getId()));

        //查询sku
        spu.setSkus(querySkuBySpuId(spu.getId()));

        //正常返回
        return spu;
    }

    private void sendMessage(Long id, String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            logger.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }

    /*
    **购物车查询sku
     */
    public Sku querySkuById(Long id) {
        return this.skuMapper.selectByPrimaryKey(id);
    }

}
