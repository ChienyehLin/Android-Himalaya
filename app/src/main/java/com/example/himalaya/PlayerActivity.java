package com.example.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.himalaya.interfaces.IPlayerControl;
import com.example.himalaya.interfaces.IPlayerViewControl;
import com.example.himalaya.services.PlayerService;
import com.example.himalaya.utils.LogUtil;

import static com.example.himalaya.interfaces.IPlayerControl.PLAYER_STATE_PAUSE;
import static com.example.himalaya.interfaces.IPlayerControl.PLAYER_STATE_PLAY;
import static com.example.himalaya.interfaces.IPlayerControl.PLAYER_STATE_STOP;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";
    private Button mButtonPlayPause;
    private Button mButtonClose;
    private SeekBar mSeekBar;
    private PlayerConnection mPlayerConnection;
    private IPlayerControl mIPlayerControl;
    private boolean isUserTouchProgressBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        requestPermission();
        //设置相关的事件
        initEvent();
        //启动播放的服务
        initService();
        //绑定服务
        initBindService();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    private void initService() {
        LogUtil.d(TAG, "StartService");
        startService(new Intent(this, PlayerService.class));
    }

    /**
     * 绑定服务，服务不可以长期运行
     */
    private void initBindService() {
        LogUtil.d(TAG, "BindService");
        Intent intent = new Intent(this, PlayerService.class);
        if (mPlayerConnection == null) {
            mPlayerConnection = new PlayerConnection();
        }
        bindService(intent, mPlayerConnection, BIND_AUTO_CREATE);
    }

    private class PlayerConnection implements ServiceConnection {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //onbind返回Null的binder时 该方法不会执行
            LogUtil.d(TAG, "连接成功 onServiceConnected return binder");
            mIPlayerControl = (IPlayerControl) service;
            //播放或者暂停
            mIPlayerControl.registerViewController(mPlayerViewControl);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "连接断开 onServiceDisconnected");
            mIPlayerControl = null;
        }
    }

    private void initEvent() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条发生改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //手已经将触摸上去拖动
                isUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int touchProgress = seekBar.getProgress();
                LogUtil.d(TAG, "touchProgress ===>" + touchProgress);
                //停止拖动
                if (mIPlayerControl != null) {
                    mIPlayerControl.seekTo(touchProgress);
                }
                isUserTouchProgressBar = false;
            }
        });
            //播放器开源框架exo player
        mButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //播放或者暂停
                if (mIPlayerControl != null) {
                    mIPlayerControl.playOrPause();
                }
            }
        });
        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭按钮被点击
                if (mIPlayerControl != null) {
                    mIPlayerControl.stop();
                }
            }
        });
    }

    private void initView() {
        mSeekBar = findViewById(R.id.seek_bar);
        mButtonPlayPause = findViewById(R.id.btn_pause_play);
        mButtonClose = findViewById(R.id.btn_close);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerConnection != null) {
            LogUtil.d(TAG, "onDestroy unbind");
            //释放资源
            mIPlayerControl.unregisterViewController();
            unbindService(mPlayerConnection);
        }
        Log.d(TAG, "onDestroy..... ");
    }

    private IPlayerViewControl mPlayerViewControl = new IPlayerViewControl() {
        @Override
        public void onPlayerStateChange(int state) {
            //我们要根据播放状态来修改Ui
            switch (state) {
                case PLAYER_STATE_PLAY:
                    //播放中的话，我们要修改按钮显示为暂停
                    mButtonPlayPause.setText("暂停");
                    break;
                case PLAYER_STATE_PAUSE:
                case PLAYER_STATE_STOP:
                    mButtonPlayPause.setText("播放");
                    break;
            }

        }

        @Override
        public void onSeekChange(final int progress) {
            Log.d(TAG,Thread.currentThread().getName());
            //改变播放进度，有一个条件  当用户的手触摸到到进度条的时候 就不更新
            //从上面的Log我们可以发现 这不是主线程 所以不可以用于更新Ui
            //为什么更新进度不会崩溃吗
            //因为在android里面有两个控件可以使用子线程去更新
            //一个是ProgressBar， 另外一个是SurafaceView
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isUserTouchProgressBar) {
                        mSeekBar.setProgress(progress);
                    }
                }
            });

        }
    };
}