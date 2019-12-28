package com.leyou.item.bo;
import com.leyou.item.domain.Sku;
import com.leyou.item.domain.Spu;
import com.leyou.item.domain.SpuDetail;
import lombok.Data;

import java.util.List;

/**
 * Spu业务领域对象
 */
@Data
public class SpuBo extends Spu {
    private String cname;
    private String bname;

    private SpuDetail spuDetail;

    private List<Sku> skus;
}
