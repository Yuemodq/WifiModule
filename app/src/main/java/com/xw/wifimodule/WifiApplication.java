package com.xw.wifimodule;

import android.app.Application;
import android.content.Context;

/**
 * 类描述：
 * 项目：WifiModule
 * 作者：xw
 * 邮箱：xw_appdev@163.com
 * 日期：2017-07-21 02:00
 */

public class WifiApplication extends Application {
    
    private static Context sContext;
    
    public static Context getContext() {
        return sContext;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}


















