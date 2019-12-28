package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

@Data
public class ExceptionResult {
    private Integer code;
    private String msg;
    private Long timeStamp;

    public ExceptionResult(ExceptionEnum em){
        this.code = em.getCode();
        this.msg = em.getMsg();
        this.timeStamp = System.currentTimeMillis();
    }
}
