package com.imooc.commons.exception;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @ExceptionHandler(Exception.class)
    public ResultInfo handlerException(Exception ex) {
        log.info("未知异常：{}", ex);
        String path = request.getRequestURI();
        ResultInfo resultInfo =
                ResultInfoUtil.buildError(path);
        return resultInfo;
    }

}