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
        // MediaPlayer?????????????????????????????????
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
     * Surface??????
     * ???Surface?????????????????????MediaPlayer????????????????????????
     * ??????????????????????????????MediaPlayer???????????????SurfaceView??????
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // ??????????????????
        initPlayer();
        // ???MediaPlayer??????SurfaceView
        // ??????: ??????????????????Surface????????????????????????
        player.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * Surface??????
     * Activity???onStop??????????????????surfaceDestroyed????????????
     * ???????????????????????????MediaPlayer?????????????????????????????????????????????????????????
     * ????????????????????????Surface?????????????????????MediaPlayer???
     * ??????MediaPlayer??????seekTo??????????????????????????????pause??????????????????
     * ?????????MediaPlayer???????????????
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // ????????????????????????
        currentPosition = player.getCurrentPosition();
        // ?????????????????????????????? ??????MediaPlayer????????????????????????????????????????????????????????? ??????????????????
        // ????????????
        pause();
        // ??????MediaPlayer
        destroyPlayer();
    }

    @Override
    public void onClick(View v) {
        if (player.isPlaying()) {
            // ????????????
            pause();
        } else {
            // ????????????
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
     * ??????????????????????????????
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // ?????????????????????????????????????????????
        player.seekTo(seekBar.getProgress());
    }

    /**
     * ???MediaPlayer???????????? ??????????????????
     *
     * @param player
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        // ???????????????????????? ?????????player????????? ????????????
        seekBar.setMax(player.getDuration());
        // ????????????
        maxView.setText(parseTime(player.getDuration()));
        // ????????????
        currentView.setText("00:00");
        // ????????????????????????
        button.setEnabled(true);
        // ??????MediaPlayer????????????????????????
        // ??????: seekTo????????????????????????currentPosition?????? ??????????????????????????????????????????
        // ???????????????OnSeekCompleteListener?????????UI ??????????????????
        player.seekTo(currentPosition);
        // ????????????Surface??????????????????????????????????????????????????????????????????
    }

    /**
     * MediaPlayer??????seekTo???????????????
     * seekTo????????????????????????????????? ??????????????????????????????????????????
     *
     * @param player
     */
    @Override
    public void onSeekComplete(MediaPlayer player) {
        // player.getCurrentPosition()???????????????????????????
        currentPosition = player.getCurrentPosition();
        // ????????????UI
        handler.sendEmptyMessage(0);
    }

    /**
     * ?????????????????????
     * ????????????????????????setOnErrorListener???onError??????false???????????????
     *
     * @param player
     */
    @Override
    public void onCompletion(MediaPlayer player) {
        // ???????????????
        stopTimer();
        // ??????????????????
        button.setText("????????????");
    }

    /**
     * ?????????????????????
     *
     * @param player
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        // ??????
        Toast.makeText(this, "???????????????" + what, Toast.LENGTH_SHORT).show();
        // ???????????????
        stopTimer();
        // ??????????????????
        button.setText("????????????");
        // ?????????true?????????onCompletion??????
        return true;
    }

    /**
     * ??????????????????
     */
    private void initPlayer() {
        try {
            // ?????????MediaPlayer
            player = new MediaPlayer();
            // ??????????????????
            player.setOnPreparedListener(this);
            // ????????????????????????
            player.setOnSeekCompleteListener(this);
            // ????????????????????????
            player.setOnCompletionListener(this);
            // ????????????????????????
            player.setOnErrorListener(this);
            // ???????????????
            AssetManager manager = getAssets();
            AssetFileDescriptor descriptor = manager.openFd(mFileName);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + mFileName);
            Log.d(TAG, "DOWN DIR11 = " + file.getAbsolutePath() );

//            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            player.setDataSource(file.getAbsolutePath());
//            Resources   mResources = getResources();
//            AssetFileDescriptor afd = mResources.openRawResourceFd(R.raw.local_video);
//            player.setDataSource( afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            // ????????????
            player.prepareAsync();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * ???????????????
     */
    private void destroyPlayer() {
        // ????????????
        if (player.isPlaying()) {
            player.stop();
        }
        // ????????????
        player.release();
        player = null;
        // ?????????????????????????????? ??????MediaPlayer???????????????
        button.setEnabled(false);
    }

    /**
     * ?????? -> ?????? HH:mm:ss
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
     * ????????????
     */
    private void start() {
        player.start();
        startTimer();
        button.setText("????????????");
    }

    /**
     * ????????????
     */
    private void pause() {
        if (player.isPlaying()) {
            player.pause();
        }
        stopTimer();
        button.setText("????????????");
    }

    /**
     * ???????????????
     */
    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // ????????????UI
                currentPosition = player.getCurrentPosition();
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 0, 500);
    }

    /**
     * ???????????????
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
     * ????????????UI
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            seekBar.setProgress(currentPosition);
            currentView.setText(parseTime(currentPosition));
        }
    };

}