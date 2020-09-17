package com.example.himalaya.interfaces;

public interface IPlayerViewControl {
    /**
     * 播放状态的通知
     * @param state
     */
    void onPlayerStateChange(int state);

    /**
     * 播放进度的改变
     * @param progress
     */
    void onSeekChange(int progress);
}
