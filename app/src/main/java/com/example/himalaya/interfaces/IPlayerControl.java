package com.example.himalaya.interfaces;

public interface IPlayerControl {
    //播放状态常量
    //播放
    static int PLAYER_STATE_PLAY = 1;
    static int PLAYER_STATE_PAUSE = 2;
    static int PLAYER_STATE_STOP = 3;

    /**
     * 把UI的控制接口设置给逻辑层
     *
     * @param iPlayerViewControl
     */
    void registerViewController(IPlayerViewControl iPlayerViewControl);

    /**
     * 取消接口通知的注册
     */
    void unregisterViewController();


    /**
     * 停止播放
     */
    void stop();

    /**
     * 设置播放进度
     *
     * @param progress 播放进度
     */
    void seekTo(int progress);

    void playOrPause();
}
