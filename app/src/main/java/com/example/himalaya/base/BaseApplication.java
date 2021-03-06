package com.example.himalaya.base;

import android.app.Application;
import android.os.Handler;
import android.os.RemoteException;

import com.example.himalaya.aidl.IMyAidlInterface;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;

import java.util.logging.LogRecord;

public class BaseApplication extends Application {

    private static Handler sHandler =null;
    @Override
    public void onCreate() {
        super.onCreate();
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if(DTransferConstants.isRelease) {
            String mAppSecret = "afe063d2e6df361bc9f1fb8bb8210d67";
            mXimalaya.setAppkey("af1d317b871e0e7e2ce45872caa34d9a");
            mXimalaya.setPackid("com.humaxdigital.automotive.ximalaya");
            mXimalaya.init(this ,mAppSecret);
        } else {
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this ,mAppSecret);
        }
        //初始化LogUtil,可以确保是应用打出来的log,改为True就可以隐藏所有log
        LogUtil.init(this.getPackageName(),false);

    }

    public  static  Handler getsHandler(){
        return sHandler;
    }
}
