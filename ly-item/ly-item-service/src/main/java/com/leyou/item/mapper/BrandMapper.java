package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.domain.Brand;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    @Insert("insert into tb_category_brand (category_id,brand_id) values (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("select b.* from tb_brand b inner join tb_category_brand cb on b.id = cb.brand_id where cb.category_id = #{cid}")
    List<Brand> queryBrandsByCid(@Param("cid") Long cid);

    @Update("UPDATE tb_category_brand SET category_id = #{cid} where brand_id = #{bid}")
    void updateCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("DELETE from tb_category_brand where brand_id = #{bid}")
    void deleteCategoryBrandByBid(@Param("bid") Long bid);

}


