package com.example.mediaplayerdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String FILE_NAME_KEY ="FILE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager. PERMISSION_GRANTED) {
            ActivityCompat. requestPermissions( this, new String[]{Manifest.permission. WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE },
                    1000);
        }
        findViewById(R.id.video_mp4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.mp4");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_3GP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.3gp");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_AVI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.avi");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_ASF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.asf");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_MOV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.mov");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_WMA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.wma");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_WMV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.wmv");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_mpeg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.mpeg");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_mpg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.mpg");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_rm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.rm");
                startActivity(intent);
            }
        });

        findViewById(R.id.video_rmvb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra(FILE_NAME_KEY, "local_video.rmvb");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //同意申请权限
            } else
            {
                // 用户拒绝申请权限
                Toast.makeText(MainActivity.this,"请同意权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}