package com.xw.wifimodule.util;

/**
 * 类描述：
 * 项目：WifiModule
 * 作者：xw
 * 邮箱：xw_appdev@163.com
 * 日期：2017-07-18 00:13
 */

public class PreConditionCheck {
    
    public static <T> Boolean checkNotNull(T obj) {
//        if (obj == null) {
//            return false;
//        }
//        return true;
        return obj != null;
    }
    
}
