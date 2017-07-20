package com.xw.wifimodule.model;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.xw.wifimodule.R;
import com.xw.wifimodule.util.ResourcesUtils;

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
     * 无密码
     */
    static final int SECURITY_NONE = 0;
    
    //wep
    static final int SECURITY_WEP = 1;
    
    //wpa-psk,wpa2-psk
    static final int SECURITY_PSK = 2;
    
    //eap
    static final int SECURITY_EAP = 3;
    
    public static final int INVALID_NETWORK_ID = -1;
    
    /**
     * Wi-Fi 信号强度
     */
    private static final int LEVEL_COUNT = 3;
    
    /**
     * Wi-Fi 名称
     */
    public String mSSID;
    
    public int mSecurity;
    
    //已保存 networkId 不为 -1，否则未保存
    public int mNetworkId = -1;
    
    /**
     * STA(station) 接入点的 MAC 地址
     */
    public String mBSSID;
    
    public String mIpAddress = "";
    
    public String mDns = "";
    
    public String mGateway = "";
    
    public String mAsk = "";
    
    public boolean mWpsAvailable = false;
    
    /**
     * Wi-Fi 配置
     */
    private WifiConfiguration mConfig;
    
    /**
     * Wi-Fi 扫描结果
     */
    private ScanResult mScanResult;
    
    /**
     * 信号强度
     */
    private int mRssi;
    
    /**
     * 处于活动状态或正在建立的 Wi-Fi 连接的状态信息
     */
    private WifiInfo mInfo;
    
    /**
     * 网络连接细分状态。
     * 在 Network.State 中粗略对应划分为四个状态：
     * Connecting, Connected,Disconnecting,Disconnected
     */
    private DetailedState mState;
    
    PskType mPskType = PskType.UNKNOWN;
    
    public AccessPoint(Context context, WifiConfiguration config) {
        loadConfig(config);
    }
    
    public AccessPoint(Context context, ScanResult result) {
        loadResult(result);
    }
    
    private void loadConfig(WifiConfiguration config) {
        mSSID = config.SSID;
        mBSSID = config.BSSID;
        mSecurity = getSecurity(config);
        mNetworkId = config.networkId;
        mRssi = Integer.MAX_VALUE;
        mConfig = config;
    }
    
    private void loadResult(ScanResult result) {
        mSSID = result.SSID;
        mBSSID = result.BSSID;
        mSecurity = getSecurity(result);
        mWpsAvailable = mSecurity != SECURITY_EAP && result.capabilities.contains("WPS");
        if (mSecurity == SECURITY_PSK) {
            mPskType = getPskType(result);
        }
        mNetworkId = INVALID_NETWORK_ID;
        mRssi = result.level;
        mScanResult = result;
    }
    
    private String removeDoubleQuotes(String string) {
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"')
                && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
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
    
    public WifiConfiguration getConfig() {
        return mConfig;
    }
    
    public WifiInfo getInfo() {
        return mInfo;
    }
    
    public DetailedState getState() {
        return mState;
    }
    
    private PskType getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa && wpa2) {
            return PskType.WPA_WPA2;
        } else if (wpa2) {
            return PskType.WPA2;
        } else if (wpa) {
            return PskType.WPA;
        }
        return PskType.UNKNOWN;
    }
    
    /**
     * 获取 Wi-Fi 加密方式
     * @param config
     * @return
     */
    private int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }
    
    /**
     * 获取 Wi-Fi 加密方式
     * @param result
     * @return
     */
    private int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }
    
    public String getSecurityString(boolean concise) {
        switch (mSecurity) {
            case SECURITY_EAP:
                return concise ?
                        ResourcesUtils.res2String(R.string.wifi_security_short_eap) :
                        ResourcesUtils.res2String(R.string.wifi_security_eap);
            case SECURITY_WEP:
                return concise ? ResourcesUtils.res2String(R.string.wifi_security_short_wep) :
                        ResourcesUtils.res2String(R.string.wifi_security_short_wep);
            case SECURITY_PSK:
                switch (mPskType) {
                    case WPA:
                        return concise ? ResourcesUtils.res2String(R.string.wifi_security_short_wpa) :
                                ResourcesUtils.res2String(R.string.wifi_security_wpa);
                    case WPA2:
                        return concise ? ResourcesUtils.res2String(R.string.wifi_security_short_wpa2) :
                                ResourcesUtils.res2String(R.string.wifi_security_wpa2);
                    case WPA_WPA2:
                        return concise ? ResourcesUtils.res2String(R.string.wifi_security_short_wpa_wpa2) :
                                ResourcesUtils.res2String(R.string.wifi_security_wpa_wpa2);
                    case UNKNOWN:
                        default:
                            return concise ? ResourcesUtils.res2String(R.string.wifi_security_short_psk_generic) :
                                    ResourcesUtils.res2String(R.string.wifi_security_psk_generic);
                }
            case SECURITY_NONE:
                default:
                    return concise ? "" :
                            ResourcesUtils.res2String(R.string.wifi_security_none);
        }
    }
    
    @Override
    public int compareTo(@NonNull AccessPoint another) {
//        AccessPoint other = another;
        // 活动的 Wi-Fi 排在前
        if (mInfo != null && another.mInfo == null) return -1;
        if (mInfo == null && another.mInfo != null) return 1;
    
        // 有信号的排在前
        if (mRssi != Integer.MAX_VALUE && another.mRssi == Integer.MAX_VALUE) return -1;
        if (mRssi == Integer.MAX_VALUE && another.mRssi != Integer.MAX_VALUE) return 1;
    
        // 配置过的排在前
        if (mNetworkId != INVALID_NETWORK_ID
                && another.mNetworkId == INVALID_NETWORK_ID) return -1;
        if (mNetworkId == INVALID_NETWORK_ID
                && another.mNetworkId != INVALID_NETWORK_ID) return 1;
    
        // 信号强的排在前
        int difference = WifiManager.compareSignalLevel(another.mRssi, mRssi);
        if (difference != 0) {
            return difference;
        }
        // 最后通过名字排列
        return mSSID.compareToIgnoreCase(another.mSSID);
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
        mConfig.SSID = convertToQuotedString(mSSID);
        mConfig.BSSID = mBSSID;
        mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) {
            return true;
        }
        if (other.getClass() == AccessPoint.class) {
            AccessPoint tempPoint = (AccessPoint) other;
            return this.mSSID.equals(tempPoint.mSSID);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = 0;
        if (mInfo != null) result += 13 * mInfo.hashCode();
        result += 19 * mRssi;
        result += 23 * mNetworkId;
        result += 29 * mSSID.hashCode();
        return result;
    }
    
    enum PskType {
        UNKNOWN,
        WPA,
        WPA2,
        WPA_WPA2
    }
}
































