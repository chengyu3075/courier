package com.sandy.courier.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description: @createTime：2020/5/29 14:50
 * @author: chengyu3
 **/
public class StringUtil {

    public static String escape(String s) {
        if (StringUtils.isBlank(s)) {
            return "";
        }
        return s.replace("\"", "").replace("\\", "");
    }

    public static void main(String[] args) {
        System.out.println(escape("环球音乐\\Eas Music"));
    }
}
