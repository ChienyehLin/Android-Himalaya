package com.example.himalaya.presenters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.himalaya.PlayerActivity;
import com.example.himalaya.R;
import com.example.himalaya.broadcast.PlayerReceiver;
import com.example.himalaya.interfaces.IPlayerControl;
import com.example.himalaya.interfaces.IPlayerViewControl;
import com.example.himalaya.services.PlayerService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.example.himalaya.broadcast.PlayerReceiver.BROADCAST_PLAY_OR_PAUSE;
import static com.example.himalaya.broadcast.PlayerReceiver.BROADCAST_STOP;

public class PlayerPresenter extends Binder implements IPlayerControl {
    private static final String TAG = "PlayerPresenter";
    private IPlayerViewControl mViewController;
    private int mCurrentState = PLAYER_STATE_STOP;
    private MediaPlayer mMediaPlayer;
    private Timer mTimer;
    private SeekTimeTask mTimeTask;
    private Notification mNotification;
    private Context mContext;
    private RemoteViews mRemoteViews;
    private NotificationManager mManager;

    public PlayerPresenter(Context context) {
        mContext = context;

    }

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
        if (mNotification != null) {
            mManager.cancel(1);
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


            mManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mManager = mContext.getSystemService(NotificationManager.class);
            }

            //设置自定义通知并发送
            Notification.Builder builder = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder = new Notification.Builder(mContext, "playerChannel");
            }
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                    R.mipmap.image);
            builder.setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(icon);
            mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.layout_notification_player);
            //设定更新播放按钮text
            Intent intent = new Intent();
            intent.setAction(BROADCAST_PLAY_OR_PAUSE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.btn_pause_play, pendingIntent);
            //设定停止关闭notification按钮
            intent = new Intent();
            intent.setAction(BROADCAST_STOP);
            pendingIntent =PendingIntent.getBroadcast(mContext, 0, intent, FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.btn_stop, pendingIntent);
            //设置播放按钮发送广播
            updateNotification("暂停");


            //设置跳转activity
            intent = new Intent(mContext, PlayerActivity.class);
            pendingIntent = PendingIntent.getActivity(mContext, 0, intent, FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setCustomContentView(mRemoteViews);
                builder.setContentIntent(pendingIntent).setStyle(new Notification.DecoratedCustomViewStyle());
            }

            //之后发送的通知只震动一次
            builder.setOnlyAlertOnce(true);
            //发送通知
            mNotification = builder.build();
            mManager.notify(1, mNotification);
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
                updateNotification("播放");
                mManager.notify(1,mNotification);
                mCurrentState = PLAYER_STATE_PAUSE;
                stopTimer();
            }
        } else if (mCurrentState == PLAYER_STATE_PAUSE) {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                updateNotification("暂停");
                mManager.notify(1,mNotification);
                mCurrentState = PLAYER_STATE_PLAY;
                startTimer();
            }
        }
        if (mViewController != null) {
            mViewController.onPlayerStateChange(mCurrentState);
        }
    }

    private void updateNotification(String buttonText) {
        mRemoteViews.setTextViewText(R.id.btn_pause_play, buttonText);


    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    return false;

                    msg.what
                }
            })

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
            if (mMediaPlayer != null && mViewController != null) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                Log.d(TAG, "current play position ====>" + mMediaPlayer.getCurrentPosition());
                int uiPosition = (int) (1.0f * currentPosition / mMediaPlayer.getDuration() * 100);
                mViewController.onSeekChange(uiPosition);
            }


        }
    }
}
