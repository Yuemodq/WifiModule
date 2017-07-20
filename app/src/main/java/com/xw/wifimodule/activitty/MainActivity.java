package com.xw.wifimodule.activitty;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.xw.wifimodule.R;
import com.xw.wifimodule.adapter.SettingWifiAdapter;
import com.xw.wifimodule.presenter.MainPresenter;
import com.xw.wifimodule.view.IMainView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SettingWifiAdapter.OnWifiItemClickListener, IMainView {
    
    @BindView(R.id.rv_wifi)
    RecyclerView mRvWifi;
    
    MainPresenter mPresenter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }
    
    private void init() {
        mPresenter = new MainPresenter();
    }
    
    @Override
    public void onWifiItemClick(TextView tvState, int position) {
        mPresenter.connectWifi();
    }
    
    @Override
    public void showConnecting() {
        
    }
    
    @Override
    public void showConnectSuccess() {
        
    }
    
    @Override
    public void showConnectFailed() {
        
    }
}




































