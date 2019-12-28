package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.domain.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        // 查询商品分类名称
        List<String> names = this.categoryClient.findNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 查询sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        // 查询详情
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        // 查询规格参数
        List<SpecParam> params = this.specificationClient.querySpecParamList(null, spu.getCid3(), true);

        // 处理sku，仅封装id、价格、标题、图片，并获得价格集合
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
        });

        // 处理规格参数
        Map<String, Object> genericSpecs = mapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> specialSpecs = mapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, Object>>() {
        });
        // 获取可搜索的规格参数
        Map<String, Object> searchSpec = new HashMap<>();

        // 过滤规格模板，把所有可搜索的信息保存到Map中
        Map<String, Object> specMap = new HashMap<>();
        params.forEach(p -> {
            if (p.getSearching()) {
                if (p.getGeneric()) {
                    String value = genericSpecs.get(p.getId().toString()).toString();
                    if(p.getNumeric()){
                          value = chooseSegment(value, p);
                    }
                    specMap.put(p.getName(), StringUtils.isBlank(value) ? "其它" : value);
                } else {
                    specMap.put(p.getName(), specialSpecs.get(p.getId().toString()));
                }
            }
        });

        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(mapper.writeValueAsString(skuList));
        goods.setSpecs(specMap);
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        QueryBuilder querry = buildBasicQueryWithFilter(request);
        queryBuilder.withQuery(querry);
        // 通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        // 分页
        searchWithPageAndSort(queryBuilder, request);

        // 聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("category").field("brandId"));

        // 执行查询获取结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        // 获取聚合结果集
        // 商品分类的聚合结果
        List<Category> categories =
                getCategoryAggResult(goodsPage.getAggregation("brands"));
        // 品牌的聚合结果
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation("category"));

        // 根据商品分类判断是否需要聚合
        List<Map<String, Object>> specs = new ArrayList<>();
        if (categories.size() == 1) {
            // 如果商品分类只有一个才进行聚合，并根据分类与基本查询条件聚合
            specs = getSpec(categories.get(0).getId(), querry);
        }

        return new SearchResult(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent(), categories, brands, specs);
    }

    /**
     * 聚合出规格参数
     *
     * @param cid
     * @param query
     * @return
     */
    private List<Map<String, Object>> getSpec(Long cid, QueryBuilder query) {
        try {
            // 不管是全局参数还是sku参数，只要是搜索参数，都根据分类id查询出来
            List<SpecParam> params = this.specificationClient.querySpecParamList(null, cid, true);
            List<Map<String, Object>> specs = new ArrayList<>();

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(query);

            // 聚合规格参数
            params.forEach(p -> {
                String key = p.getName();
                queryBuilder.addAggregation(AggregationBuilders.terms(key).field("specs." + key + ".keyword"));

            });

            // 查询
            Map<String, Aggregation> aggs = this.elasticsearchTemplate.query(queryBuilder.build(),
                    SearchResponse::getAggregations).asMap();

            // 解析聚合结果
            params.forEach(param -> {
                Map<String, Object> spec = new HashMap<>();
                String key = param.getName();
                spec.put("key", key);
                StringTerms terms = (StringTerms) aggs.get(key);
                spec.put("value", terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString));
                specs.add(spec);
            });

            return specs;
        } catch (
                Exception e)

        {
            logger.error("规格聚合出现异常：", e);
            return null;
        }

    }

    // 构建基本查询条件
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder, SearchRequest request) {
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();

        // 1、分页
        queryBuilder.withPageable(PageRequest.of(page - 1, size));
        // 2、排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            // 如果不为空，则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
    }
    // 构建基本查询条件
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));

        // 添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())){
            return boolQueryBuilder;
        }
        for (Map.Entry<String, String> entry : request.getFilter().entrySet()) {

            String key = entry.getKey();
            // 如果过滤条件是“品牌”, 过滤的字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            } else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }

        return boolQueryBuilder;
    }
    // 解析品牌聚合结果
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        try {
            LongTerms brandAgg = (LongTerms) aggregation;
            List<Long> bids = new ArrayList<>();
            for (LongTerms.Bucket bucket : brandAgg.getBuckets()) {
                bids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询品牌
            return this.brandClient.queryBrandsByIds(bids);
        } catch (Exception e) {
            logger.error("品牌聚合出现异常：", e);
            return null;
        }
    }

    // 解析商品分类聚合结果
    private List<Category> getCategoryAggResult(Aggregation aggregation) {
        try {
            List<Category> categories = new ArrayList<>();
            LongTerms categoryAgg = (LongTerms) aggregation;
            List<Long> cids = new ArrayList<>();
            for (LongTerms.Bucket bucket : categoryAgg.getBuckets()) {
                cids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询分类名称
            List<String> names = this.categoryClient.findNamesByIds(cids);

            for (int i = 0; i < names.size(); i++) {
                Category c = new Category();
                c.setId(cids.get(i));
                c.setName(names.get(i));
                categories.add(c);
            }
            return categories;
        } catch (Exception e) {
            logger.error("分类聚合出现异常：", e);
            return null;
        }
    }

    public void createIndex(Long id) throws IOException {

        Spu spu = this.goodsClient.querySpuById(id);

        // 构建商品
        Goods goods = this.buildGoods(spu);

        // 保存数据到索引库
        this.goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}