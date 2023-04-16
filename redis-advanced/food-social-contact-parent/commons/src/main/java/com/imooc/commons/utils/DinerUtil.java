package com.imooc.commons.utils;

import cn.hutool.core.bean.BeanUtil;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.vo.SignInDinerInfo;

import java.util.LinkedHashMap;

/**
 * @author E.T
 * @date 2023/4/16
 */
public class DinerUtil {

    public static SignInDinerInfo transToSignInDinerInfo(ResultInfo resultInfo) {
        SignInDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
                new SignInDinerInfo(), false);
        return dinerInfo;
    }

}
