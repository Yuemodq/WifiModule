package com.xw.wifimodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xw.wifimodule.R;
import com.xw.wifimodule.adapter.holder.SettingWifiHolder;
import com.xw.wifimodule.model.AccessPoint;
import com.xw.wifimodule.util.PreConditionCheck;

import java.util.List;

/**
 * 类描述：
 * 项目：WifiModule
 * 作者：xw
 * 邮箱：xw_appdev@163.com
 * 日期：2017-07-17 23:47
 */

public class SettingWifiAdapter extends RecyclerView.Adapter<SettingWifiHolder> {
    
    private Context mContext;
    
    private List<AccessPoint> mAssessPoints;
    
    private OnWifiItemClickListener mWifiItemClickListener;
    
    public SettingWifiAdapter(Context context) {
        this.mContext = context;
    }
    
    @Override
    public SettingWifiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_setting_wifi, parent, false);
        return new SettingWifiHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(final SettingWifiHolder holder, final int position) {
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreConditionCheck.checkNotNull(mWifiItemClickListener)) {
                    mWifiItemClickListener.onWifiItemClick(holder.mTvWifiState, position);
                }
            }
        });
        
    }
    
    @Override
    public int getItemCount() {
        if (PreConditionCheck.checkNotNull(mAssessPoints)) {
            return mAssessPoints.size();
        }
        return 0;
    }
    
    public void setWifiItemClickListener(OnWifiItemClickListener wifiItemClickListener) {
        mWifiItemClickListener = wifiItemClickListener;
    }
    
    public interface OnWifiItemClickListener {
        void onWifiItemClick(TextView tvState, int position);
    }
}
















