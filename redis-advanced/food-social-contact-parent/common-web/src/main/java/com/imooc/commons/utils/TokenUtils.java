package com.imooc.commons.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author E.T
 * @date 2023/3/29
 */
public class TokenUtils {
    public static String parseToken(HttpServletRequest request) {

        String token = request.getParameter("access_token");
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(token)) {
            token = token.replace("Bearer ", "");
        }
        return token;
    }
}
