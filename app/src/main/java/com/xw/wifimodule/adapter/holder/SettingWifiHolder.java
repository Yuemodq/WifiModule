package com.xw.wifimodule.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xw.wifimodule.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 类描述：
 * 项目：WifiModule
 * 作者：xw
 * 邮箱：xw_appdev@163.com
 * 日期：2017-07-17 23:48
 */

public class SettingWifiHolder extends RecyclerView.ViewHolder {
    
    @BindView(R.id.tv_wifi_level)
    public TextView mTvWifiLevel;
    
    @BindView(R.id.tv_wifi_name)
    public TextView mTvWifiName;
    
    @BindView(R.id.tv_wifi_state)
    public TextView mTvWifiState;
    
    @BindView(R.id.tv_wifi_security)
    public TextView mTvWifiSecurity;
    
    public SettingWifiHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(itemView);
    }
}


























