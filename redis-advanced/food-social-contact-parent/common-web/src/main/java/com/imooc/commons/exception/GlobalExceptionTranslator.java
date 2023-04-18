package com.imooc.commons.exception;

import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.BaseResponse;
import com.imooc.commons.model.ErrorMessage;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author E.T
 * @date 2022/12/5
 */

@RestControllerAdvice // 将输出的内容写入 ResponseBody 中
@Slf4j
public class GlobalExceptionTranslator {
    @Resource
    private HttpServletRequest request;

    @ExceptionHandler(ParameterException.class)
    public ResultInfo handlerParameterException(ParameterException ex) {
        String path = request.getRequestURI();

        ResultInfo resultInfo =
                ResultInfoUtil.buildError(ex.getErrorCode(), ex.getMessage(), path);
        return resultInfo;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handleError(MethodArgumentNotValidException e) {
        log.warn("Method Argument Not Valid", e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String message = String.format("%s:%s", error.getField(), error.getDefaultMessage());
        return BaseResponse
                .builder()
                .code(ApiConstant.ERROR_CODE)
                .message(message)
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ResultInfo handlerException(Exception ex) {
        log.info("未知异常：{}", ex);
        String path = request.getRequestURI();
        ResultInfo resultInfo =
                ResultInfoUtil.buildError(path);
        return resultInfo;
    }

}