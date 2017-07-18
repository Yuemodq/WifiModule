package com.xw.wifimodule.model;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 类描述：
 * 项目：WifiModule
 * 作者：xw
 * 邮箱：xw_appdev@163.com
 * 日期：2017-07-18 00:10
 */

public class AccessPoint implements Comparable<AccessPoint> {

    private static final long serialVersionUID = 1L;

    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    private static final int LEVEL_COUNT = 3;
    private static final String KEY_DETAILEDSTATE = "key_detailedstate";
    private static final String KEY_WIFIINFO = "key_wifiinfo";
    private static final String KEY_SCANRESULT = "key_scanresult";
    private static final String KEY_CONFIG = "key_config";

    public String mSsid;
    //用于存储和UI交互时输入的密码！
    @Deprecated
    public String psk;
    public int mSecurity;
    public int mNetworkId = -1;
    public String ipAddr = "";
    public String Dns = "";
    public String gateway = "";
    public String mask = "";
    boolean mWpsAvailable = false;

    private PskType mPskType = PskType.UNKNOWN;

    private String mBssid;
    private WifiConfiguration mConfig;
    private ScanResult mScanResult;
    private int mRssi;
    private WifiInfo mInfo;
    private NetworkInfo.DetailedState mState;
    private Context mContext;
//    private OnDataChangeListener mOnDataChangeListener;

    public AccessPoint(Context context, WifiConfiguration config) {
        mContext = context;
        mConfig = config;
    }

    public AccessPoint(Context context, ScanResult result) {
        mContext = context;
        mScanResult = result;
    }

    public int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return config.wepKeys[0] != null ? SECURITY_WEP : SECURITY_NONE;
    }

    public int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        } else {
            return SECURITY_NONE;
        }
    }

    private PskType getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return PskType.WPA_WPA2;
        } else if (wpa2) {
            return PskType.WPA2;
        } else if (wpa) {
            return PskType.WPA;
        } else {
//            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PskType.UNKNOWN;
        }
    }

    private String removeDoubleQuotes(String str) {
        int length = str.length();
        if (length > 1 && (str.charAt(0) == '"') &&
                (str.charAt(length - 1) == '"')) {
            return str.substring(0, length - 1);
        }
        return str;
    }

    public String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    public int getLevel() {
        if (mRssi == Integer.MAX_VALUE) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(mRssi, LEVEL_COUNT);
    }

    @Override
    public int compareTo(AccessPoint another) {
        AccessPoint other = another;
        // Active one goes first.
        if (mInfo != null && other.mInfo == null) return -1;
        if (mInfo == null && other.mInfo != null) return 1;

        // Reachable one goes before unreachable one.
        if (mRssi != Integer.MAX_VALUE && other.mRssi == Integer.MAX_VALUE) return -1;
        if (mRssi == Integer.MAX_VALUE && other.mRssi != Integer.MAX_VALUE) return 1;

        // Configured one goes before un-configured one.
        if (mNetworkId != -1
                && other.mNetworkId == -1) return -1;
        if (mNetworkId == -1
                && other.mNetworkId != -1) return 1;

        // Sort by signal strength.
        int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
        if (difference != 0) {
            return difference;
        }
        // Sort by mSsid.
        return mSsid.compareToIgnoreCase(other.mSsid);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) {
            return true;
        }
        if (other.getClass() == AccessPoint.class) {
            AccessPoint tempPoint = (AccessPoint) other;
            return this.mSsid.equals(tempPoint.mSsid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (mInfo != null) result += 13 * mInfo.hashCode();
        result += 19 * mRssi;
        result += 23 * mNetworkId;
        result += 29 * mSsid.hashCode();
        return result;
    }

    public boolean update(ScanResult result) {
        if (mSsid.equals(result.SSID) && mSecurity == getSecurity(result)) {
            if (WifiManager.compareSignalLevel(result.level, mRssi) > 0) {
                int oldLevel = getLevel();
                mRssi = result.level;
                if (getLevel() != oldLevel) {
//                    notifyChanged();
//                    if (mOnDataChangeListener != null) {
//                        mOnDataChangeListener.onDataChanged();
//                    }
                }
            }
            // This flag only comes from scans, is not easily saved in config
            if (mSecurity == SECURITY_PSK) {
                mPskType = getPskType(result);
            }
//            refreshUiData();
            return true;
        }
        return false;
    }

    public void update(WifiInfo info, NetworkInfo.DetailedState state) {
        boolean reorder = false;
        if (info != null && mNetworkId != -1
                && mNetworkId == info.getNetworkId()) {
            reorder = (mInfo == null);
            mRssi = info.getRssi();
            mInfo = info;
            mState = state;
//            refreshUiData();
        } else if (mInfo != null) {
            reorder = true;

            //???
//            mInfo = null;
//            mState = null;
//            refreshUiData();
        }
//        if (reorder) {
//            if (mOnDataChangeListener != null) {
//                mOnDataChangeListener.onDataHierarchyChanged();
//            }
//        }
    }

    public WifiConfiguration getConfig() {
        return mConfig;
    }

    public WifiInfo getInfo() {
        return mInfo;
    }

    public NetworkInfo.DetailedState getState() {
        return mState;
    }

    /**
     * Generate and save a default wifiConfiguration with common values.
     * Can only be called for unsecured networks.
     */
    public void generateOpenNetworkConfig() {
        if (mSecurity != SECURITY_NONE)
            throw new IllegalStateException();
        if (mConfig != null)
            return;
        mConfig = new WifiConfiguration();
        mConfig.SSID = convertToQuotedString(mSsid);
        mConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    }

    private enum PskType {
        UNKNOWN,
        WPA,
        WPA2,
        WPA_WPA2
    }
}
































