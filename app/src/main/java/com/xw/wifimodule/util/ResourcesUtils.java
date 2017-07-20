package com.xw.wifimodule.util;

import com.xw.wifimodule.WifiApplication;

/**
 * 类描述：
 * 项目：WifiModule
 * 作者：xw
 * 邮箱：xw_appdev@163.com
 * 日期：2017-07-21 01:58
 */

public class ResourcesUtils {
    
    public static String res2String(int resId) {
        return WifiApplication.getContext().getString(resId);
    }
    
}
