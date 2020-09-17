package com.example.himalaya.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.himalaya.interfaces.IPlayerControl;
import com.example.himalaya.interfaces.IPlayerViewControl;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;

public class PlayerService extends Service {

    private static final String TAG = "PlayerService";
    private PlayerPresenter mPlayerPresenter;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "service onCreate" );
        super.onCreate();
        if (mPlayerPresenter == null) {
            mPlayerPresenter = new PlayerPresenter();
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
        super.onDestroy();
    }
}
