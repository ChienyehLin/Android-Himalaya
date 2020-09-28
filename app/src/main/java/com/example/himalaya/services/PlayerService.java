package com.example.himalaya.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.himalaya.broadcast.PlayerReceiver;
import com.example.himalaya.interfaces.IPlayerControl;
import com.example.himalaya.interfaces.IPlayerViewControl;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;

public class PlayerService extends Service {

    private static final String TAG = "PlayerService";
    private PlayerPresenter mPlayerPresenter;
    private PlayerReceiver mPlayerReceiver;
    @Override
    public void onCreate() {
        LogUtil.d(TAG, "service " );
        super.onCreate();
        if (mPlayerPresenter == null) {
            mPlayerPresenter = new PlayerPresenter(getApplicationContext());
            //注册广播

            //第一步,创建意图过滤器
            IntentFilter intentFilter = new IntentFilter();
            //第二步,添加要监听的广播action
            intentFilter.addAction(PlayerReceiver.BROADCAST_PLAY_OR_PAUSE);
            intentFilter.addAction(PlayerReceiver.BROADCAST_STOP);
            //第三步,创建广播接收者,并且设置成成员变量,以便于取消注册,释放资源
            if (mPlayerReceiver == null) {
                mPlayerReceiver = new PlayerReceiver(mPlayerPresenter);
            }
            //第四步,注册广播接收者
            this.registerReceiver(mPlayerReceiver, intentFilter);
            //注册广播
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "service onBind" );
        return mPlayerPresenter;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "service onDestroy" );
        mPlayerPresenter = null;
        mPlayerReceiver= null;
        unregisterReceiver(mPlayerReceiver);
        super.onDestroy();
    }
}
