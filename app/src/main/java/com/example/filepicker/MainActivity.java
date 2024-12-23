package com.example.filepicker;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filepicker.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imgv;
    VideoView videov;
    MediaPlayer mediaPlayer;
    boolean isPlaying = false;
    Button pauseButton , resumeButton , stopButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgv = findViewById(R.id.imgv);
        videov = findViewById(R.id.videov);
        mediaPlayer = new MediaPlayer();
        pauseButton = findViewById(R.id.pauseButton);
        resumeButton = findViewById(R.id.resumeButton);
        stopButton = findViewById(R.id.stopButton);

        mediaPlayer.setOnCompletionListener(mp -> {
            mp.reset();
            isPlaying = false;
        });
    }

    int requestcode = 1;

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        Context context = getApplicationContext();
        if (resultcode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            String fileType = getContentResolver().getType(uri);

            if (fileType != null) {
                switch (fileType.split("/")[0]) {
                    case "image":
                        imgv.setVisibility(View.VISIBLE);
                        videov.setVisibility(View.GONE);
                        pauseButton.setVisibility(View.GONE);
                        resumeButton.setVisibility(View.GONE);
                        stopButton.setVisibility(View.GONE);
                        mediaPlayer.reset();
                        imgv.setImageURI(uri);
                        break;

                    case "video":
                        imgv.setVisibility(View.GONE);
                        pauseButton.setVisibility(View.GONE);
                        resumeButton.setVisibility(View.GONE);
                        stopButton.setVisibility(View.GONE);
                        videov.setVisibility(View.VISIBLE);
                        mediaPlayer.reset();
                        videov.setVideoURI(uri);
                        videov.start();
                        break;

                    case "audio":
                        imgv.setVisibility(View.GONE);
                        videov.setVisibility(View.GONE);

                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(getApplicationContext(), uri);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            pauseButton.setVisibility(View.VISIBLE);
                            resumeButton.setVisibility(View.VISIBLE);
                            stopButton.setVisibility(View.VISIBLE);
                            isPlaying = true; // تغییر وضعیت پخش
                        } catch (IOException e) {
                            Toast.makeText(context, "خطا در پخش فایل صوتی", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        break;

                    default:
                        Toast.makeText(context, "نوع فایل پشتیبانی نمی‌شود", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    public void pauseAudio(View view) {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void stopAudio(View view) {
        if (isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPlaying = false;
        }
    }

    public void resumeAudio(View view) {
        if (!isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }


    public void openfilechooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestcode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
