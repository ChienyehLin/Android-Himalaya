package com.example.himalaya.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.himalaya.presenters.PlayerPresenter;

public class PlayerReceiver extends BroadcastReceiver {
    public  final static  String BROADCAST_PLAY_OR_PAUSE ="broadcast_play_or_pause";
    public  final static  String BROADCAST_STOP ="broadcast_stop";
    private final PlayerPresenter mPlayerPresenter;
    public  PlayerReceiver(PlayerPresenter playerPresenter){
        mPlayerPresenter = playerPresenter;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case BROADCAST_PLAY_OR_PAUSE:
                mPlayerPresenter.playOrPause();
                Log.d("TAG","收到广播");
                break;
            case BROADCAST_STOP:
                mPlayerPresenter.stop();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + intent.getAction());
        }

    }
}
