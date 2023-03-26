package com.imooc.commons.utils;


import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.domain.ResultInfo;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * 公共返回对象工具类
 */
public class ResultInfoUtil {

    /**
     * 请求出错返回
     *
     * @param path 请求路径
     * @return
     */
    public static ResultInfo buildError(String path) {
        return build(ApiConstant.ERROR_CODE,
                ApiConstant.ERROR_MESSAGE, path, null);
    }
    public static ResultInfo buildError(String path, String message) {
        return build(ApiConstant.ERROR_CODE,
                message, path, null);
    }

    /**
     * 请求出错返回
     *
     * @param errorCode 错误代码
     * @param message   错误提示信息
     * @param path      请求路径
     * @return
     */
    public static ResultInfo buildError(int errorCode, String message, String path) {
        return build(errorCode, message, path, null);
    }

    /**
     * 请求成功返回
     *
     * @param path 请求路径
     * @return
     */
    public static ResultInfo buildSuccess(String path) {
        return build(ApiConstant.SUCCESS_CODE,
                ApiConstant.SUCCESS_MESSAGE, path, null);
    }

    /**
     * 构造仅包含响应代码及响应数据的返回值。
     * @param data
     * @return
     */
    public static ResultInfo buildDataSuccess(Object data) {
        return build(ApiConstant.SUCCESS_CODE, null, null, data);
    }


    /**
     * 请求成功返回
     *
     * @param path 请求路径
     * @param data 返回数据对象
     * @return
     */
    public static ResultInfo buildSuccess(String path, Object data) {
        return build(ApiConstant.SUCCESS_CODE,
                ApiConstant.SUCCESS_MESSAGE, path, data);
    }

    /**
     * 构建返回对象方法
     *
     * @param code
     * @param message
     * @param path
     * @param data
     * @return
     */
    public static ResultInfo build(Integer code, String message, String path, Object data) {
        if (code == null) {
            code = ApiConstant.SUCCESS_CODE;
        }
        if (message == null) {
            message = ApiConstant.SUCCESS_MESSAGE;
        }
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(code);
        resultInfo.setMessage(message);
        resultInfo.setPath(path);
        resultInfo.setData(data);
        return resultInfo;
    }

    public static ResultInfo buildError(BindingResult errors) {
        assert errors != null;
        var result = new ResultInfo();
        result.setCode(ApiConstant.ERROR_CODE);
        var data = new TreeMap<>();
        result.setData(data);
        errors.getFieldErrors().forEach(err -> {
            data.put(err.getField(), err.getDefaultMessage());
        });
        return result;
    }
}