package com.xw.wifimodule.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 类描述：全局异常捕获
 * 项目：IdeaTest
 * 作者：xw
 * 邮箱：xw_appdev@163.com
 * 日期：2017-07-18 02:07
 */

public class CrashHandle implements Thread.UncaughtExceptionHandler {
    
    private static final String TAG = CrashHandle.class.getSimpleName();
    
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    
    private Context mContext;
    
    private Map<String, String> mExceptionParamsMap;
    
    private SimpleDateFormat mDateFormat;
    
    private static CrashHandle sInstance;
    
    private CrashHandle() {
    }
    
    public static synchronized CrashHandle getInstance() {
        if (null == sInstance) {
            synchronized (CrashHandle.class) {
                if (null == sInstance) {
                    sInstance = new CrashHandle();
                }
            }
        }
        return sInstance;
    }
    
    public void init(Context context) {
        mContext = context;
        mExceptionParamsMap = new HashMap<>();
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        } else {
//            try {
//                Thread
//            }
            Log.e(TAG, e.getMessage());
        }
    }
    
    private boolean handleException(Throwable e) {
        if (null == e) {
            return false;
        }
        collectDeviceInfo(mContext);
        addCustomInfo();
//        new Thread() {
//            @Override
//            public void run() {
//
//                Looper.prepare();
//                Toast.makeText(mContext, "客官，你的程序挂掉了...", Toast.LENGTH_SHORT).show();
//                Looper.loop();
//
//            }
//        }.start();
        saveCrashLog2File(e);
        return true;
    }
    
    private void collectDeviceInfo(Context context) {
        //获取设备 packageName，versionCode
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                mExceptionParamsMap.put("versionName", versionName);
                mExceptionParamsMap.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occurred when collect package info", e);
        }
    
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                mExceptionParamsMap.put(field.getName(), field.get(null).toString());
            } catch (IllegalAccessException e) {
                Log.e(TAG, "an error occurred when collect crash info", e);
            }
        }
    }
    
    private void addCustomInfo() {
        
    }
    
    /**
     * 保存 crash 日志
     * @param e
     */
    private String saveCrashLog2File(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mExceptionParamsMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }
    
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = mDateFormat.format(new Date());
            String fileName = "crash-" + time + " - " + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (IOException e1) {
            Log.e(TAG, "an error occurred while writing file...", e1);
        }
        return null;
    }
}
























