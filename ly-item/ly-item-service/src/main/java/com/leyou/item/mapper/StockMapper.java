package com.leyou.item.mapper;

import com.leyou.item.domain.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface StockMapper extends Mapper<Stock>,IdListMapper<Stock,Long>,InsertListMapper<Stock>, DeleteByIdListMapper<Stock,Long> {

    @Update("UPDATE tb_stock SET stock = stock - #{num} WHERE sku_id = #{id} AND stock >= #{num}")
    int decreaseStock(@Param("id") Long id, @Param("num") Integer num);
}
