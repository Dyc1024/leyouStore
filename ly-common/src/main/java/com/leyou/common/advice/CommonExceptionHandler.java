package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ExceptionResult> handleLyException(LyException ex){
        return ResponseEntity.status(ex.getEnums().getCode()).body(new ExceptionResult(ex.getEnums()));
    }

}
