package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400,"商品价格不能为空"),
    CATEGORY_NOT_FOUND(404,"没有查询到指定parentid的目录"),
    BRAND_NOT_FOUND(404,"没有查询到指定条件的品牌"),
    SAVE_BRAND_ERROR(500,"新增品牌失败"),
    FILE_UPLOAD_ERROR(500,"文件上传失败"),
    FILE_TYPE_NOT_ALLOW(400,"文件类型不匹配"),
    SPEC_GROUP_NOT_FOUND(404,"没有查询到相应规格组"),
    SPEC_PARAM_NOT_FOUND(404,"没有查询到相应规格参数"),
    SAVE_SPEC_GROUP_ERROR(500,"新增规格分组失败"),
    UPDATE_SPEC_GROUP_ERROR(500,"修改规格分组失败"),
    DELETE_SPEC_GROUP_ERROR(500,"删除规格分组失败"),
    SAVE_SPEC_PARAM_ERROR(500,"新增规格参数失败"),
    UPDATE_SPEC_PARAM_ERROR(500,"修改规格参数失败"),
    DELETE_SPEC_PARAM_ERROR(500,"删除规格参数失败"),
    SPU_NOT_FOUND(404,"没有查询到指定SPU"),
    SAVE_GOODS_ERROR(500,"新增商品失败"),
    SPUDETAIL_NOT_FOUND(404,"没有查询到相应SPUDetail"),
    SKU_NOT_FOUND(404,"没有查询到相应SKU"),
    GOODS_STOCK_NOT_FOUND(404,"没有查询到相应商品库存"),
    GOODS_DELETE_ERROR(500,"删除品牌失败")
    ;
    private Integer code;
    private String msg;
}
