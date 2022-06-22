package com.example.mediaplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener {
    private static final String TAG = "SSSS.VideoActivity";

    private SurfaceView surfaceView;
    private SeekBar seekBar;
    private TextView currentView, maxView;
    private Button button;
    private TextView fileNameTV;

    private MediaPlayer player;
    private Timer timer;
    private TimerTask timerTask;
    private int currentPosition;
    String mFileName = "local_video.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        currentView = (TextView) findViewById(R.id.current);
        maxView = (TextView) findViewById(R.id.max);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        fileNameTV = findViewById(R.id.tv_file_name);
        // MediaPlayer没准备好，不能点击播放
        button.setEnabled(false);
        Intent intent = getIntent();
        if (intent != null) {
            String fileName = intent.getStringExtra(MainActivity.FILE_NAME_KEY);
            if (!TextUtils.isEmpty(fileName)) {
                mFileName = fileName;
                Log.d(TAG, "file name = " + mFileName);
                fileNameTV.setText(mFileName);

            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String fileName = intent.getStringExtra(MainActivity.FILE_NAME_KEY);
            if (!TextUtils.isEmpty(fileName)) {
                mFileName = fileName;
                Log.d(TAG, "file name = " + mFileName);
                fileNameTV.setText(mFileName);

            }
        }
    }

    /**
     * Surface创建
     * 在Surface销毁时，要销毁MediaPlayer，否则会出现问题
     * 所以这里要重新初始化MediaPlayer，并重新与SurfaceView关联
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 初始化播放器
        initPlayer();
        // 让MediaPlayer关联SurfaceView
        // 注意: 一定要在要在Surface创建成功才能关联
        player.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * Surface销毁
     * Activity的onStop方法调用时，surfaceDestroyed也会调用
     * 我们要在这一步销毁MediaPlayer，并记录上次播放的位置，否则会出现问题
     * 本人测试的时候，Surface销毁时没有销毁MediaPlayer，
     * 发现MediaPlayer调用seekTo方法无效，也不能调用pause方法停止播放
     * 也就说MediaPlayer不受控制了
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 记录上次播放位置
        currentPosition = player.getCurrentPosition();
        // 这里可以记录播放状态 等到MediaPlayer再次创建并准备就绪时，可以调用这个状态 选择是否播放
        // 暂停播放
        pause();
        // 销毁MediaPlayer
        destroyPlayer();
    }

    @Override
    public void onClick(View v) {
        if (player.isPlaying()) {
            // 暂停播放
            pause();
        } else {
            // 开始播放
            start();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 手指滑动进度条时调用
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 让播放器播放到进度条移动的位置
        player.seekTo(seekBar.getProgress());
    }

    /**
     * 当MediaPlayer准备就绪 解析资源完成
     *
     * @param player
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        // 进度条设置最大值 也就是player的时长 单位毫秒
        seekBar.setMax(player.getDuration());
        // 视频时长
        maxView.setText(parseTime(player.getDuration()));
        // 当前时长
        currentView.setText("00:00");
        // 设置可以点击状态
        button.setEnabled(true);
        // 设置MediaPlayer播放到上次的位置
        // 注意: seekTo不一定准确定位到currentPosition位置 跳转点必须是视频文件的关键帧
        // 所以我们要OnSeekCompleteListener中更新UI 这才是正确的
        player.seekTo(currentPosition);
        // 这里根据Surface销毁时记录的状态，选择是否播放，可以自己实现
    }

    /**
     * MediaPlayer调用seekTo方法后调用
     * seekTo不一定定位到准确的位置 跳转点必须是视频文件的关键帧
     *
     * @param player
     */
    @Override
    public void onSeekComplete(MediaPlayer player) {
        // player.getCurrentPosition()才是真正移动的进度
        currentPosition = player.getCurrentPosition();
        // 更新进度UI
        handler.sendEmptyMessage(0);
    }

    /**
     * 播放完成时调用
     * 出错时，没有设置setOnErrorListener或onError返回false时也会调用
     *
     * @param player
     */
    @Override
    public void onCompletion(MediaPlayer player) {
        // 停止定时器
        stopTimer();
        // 设置重新播放
        button.setText("重新播放");
    }

    /**
     * 播放出错时调用
     *
     * @param player
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        // 提示
        Toast.makeText(this, "播放错误：" + what, Toast.LENGTH_SHORT).show();
        // 停止定时器
        stopTimer();
        // 设置重新播放
        button.setText("重新播放");
        // 不返回true会调用onCompletion方法
        return true;
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        try {
            // 初始化MediaPlayer
            player = new MediaPlayer();
            // 设置准备监听
            player.setOnPreparedListener(this);
            // 设置进度完成监听
            player.setOnSeekCompleteListener(this);
            // 设置播放完成监听
            player.setOnCompletionListener(this);
            // 设置播放错误监听
            player.setOnErrorListener(this);
            // 设置播放源
            AssetManager manager = getAssets();
            AssetFileDescriptor descriptor = manager.openFd(mFileName);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + mFileName);
            Log.d(TAG, "DOWN DIR11 = " + file.getAbsolutePath() );

//            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            player.setDataSource(file.getAbsolutePath());
//            Resources   mResources = getResources();
//            AssetFileDescriptor afd = mResources.openRawResourceFd(R.raw.local_video);
//            player.setDataSource( afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            // 异步准备
            player.prepareAsync();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 销毁播放器
     */
    private void destroyPlayer() {
        // 暂停播放
        if (player.isPlaying()) {
            player.stop();
        }
        // 释放资源
        player.release();
        player = null;
        // 设置按钮不能点击状态 毕竟MediaPlayer已经销毁了
        button.setEnabled(false);
    }

    /**
     * 毫秒 -> 时间 HH:mm:ss
     *
     * @param position
     * @return
     */
    private String parseTime(int position) {
        int hour = position / 1000 / 3600;
        int minute = position / 1000 % 3600 / 60;
        int second = position / 1000 % 3600 % 60;
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
    }

    /**
     * 开始播放
     */
    private void start() {
        player.start();
        startTimer();
        button.setText("暂停播放");
    }

    /**
     * 暂停播放
     */
    private void pause() {
        if (player.isPlaying()) {
            player.pause();
        }
        stopTimer();
        button.setText("开始播放");
    }

    /**
     * 开始定时器
     */
    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 更新进度UI
                currentPosition = player.getCurrentPosition();
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 0, 500);
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 更新进度UI
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            seekBar.setProgress(currentPosition);
            currentView.setText(parseTime(currentPosition));
        }
    };

}