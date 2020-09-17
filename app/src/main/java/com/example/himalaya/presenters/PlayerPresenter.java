package com.example.himalaya.presenters;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;

import com.example.himalaya.R;
import com.example.himalaya.interfaces.IPlayerControl;
import com.example.himalaya.interfaces.IPlayerViewControl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerPresenter extends Binder implements IPlayerControl {
    private static final String TAG = "PlayerPresenter";
    private IPlayerViewControl mViewController;
    private int mCurrentState = PLAYER_STATE_STOP;
    private MediaPlayer mMediaPlayer;
    private Timer mTimer;
    private SeekTimeTask mTimeTask;

    @Override
    public void registerViewController(IPlayerViewControl iPlayerViewControl) {
        this.mViewController = iPlayerViewControl;
    }

    @Override
    public void unregisterViewController() {
        mViewController = null;
    }

    @Override
    public void stop() {
        Log.d(TAG, "Stop");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mCurrentState = PLAYER_STATE_STOP;
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
        stopTimer();
        if (mViewController != null) {
            mViewController.onPlayerStateChange(mCurrentState);
        }
    }

    @Override
    public void seekTo(int progress) {
        Log.d("TAG", "SeekTo");

        //0-100需要做一个转换,得到的seek其实是一个百分比
        int tarSeek;
        if (mMediaPlayer != null) {
            tarSeek = (int) (progress * 1.0f / 100 * mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(tarSeek);
        }

    }

    @Override
    public void playOrPause() {
        Log.d("TAG", "PlayOrPause");
        if (mCurrentState == PLAYER_STATE_STOP) {
            //创建播放器
            initPlayer();
            //设置数据源
            try {
                mMediaPlayer.setDataSource("/mnt/sdcard/dynamite.mp3");
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mCurrentState = PLAYER_STATE_PLAY;
                startTimer();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (mCurrentState == PLAYER_STATE_PLAY) {
            //如果当前的状态是播放， 那我们就暂停
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
                mCurrentState = PLAYER_STATE_PAUSE;
                stopTimer();
            }
        } else if (mCurrentState == PLAYER_STATE_PAUSE) {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                mCurrentState = PLAYER_STATE_PLAY;
                startTimer();
            }
        }
        if (mViewController != null) {
            mViewController.onPlayerStateChange(mCurrentState);
        }
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        }
    }

    /**
     * 开启一个timerTask
     */
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimeTask == null) {
            mTimeTask = new SeekTimeTask();
        }
        mTimer.schedule(mTimeTask, 0, 500);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeTask != null) {
            mTimeTask.cancel();
            mTimeTask = null;
        }
    }

    private class SeekTimeTask extends TimerTask {

        @Override
        public void run() {
            //获取当前的播放进度
            if (mMediaPlayer != null&&mViewController !=null) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                Log.d(TAG, "current play position ====>" + mMediaPlayer.getCurrentPosition());
                int uiPosition = (int) (1.0f*currentPosition/mMediaPlayer.getDuration()*100);
                    mViewController.onSeekChange(uiPosition);
            }


        }
    }
}
