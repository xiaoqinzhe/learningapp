package com.example.learningapp.media;

import androidx.appcompat.app.AppCompatActivity;

import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.learningapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VideoViewActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

//    VideoView videoView;
//    MediaController mediaController;

    SurfaceView surfaceViewVideo;
    Button btnPlay;
    MediaPlayer mediaPlayer;

    boolean isPrepared = false;
    boolean isPlaying = false;

    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        btnPlay = findViewById(R.id.btn_video_play);

        surfaceViewVideo = findViewById(R.id.sv_video_surface);
        mediaPlayer = new MediaPlayer();
        surfaceViewVideo.getHolder().setKeepScreenOn(true);

        surfaceViewVideo.getHolder().addCallback(this);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mediaPlayer.setAudioAttributes(audioAttributes);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        try {
            prepareVideo();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void prepareVideo() throws IOException {
        Uri uri = getVideoUri();
        mediaPlayer.setDataSource(this, uri);
        mediaPlayer.prepareAsync();
        isPrepared = false;
    }

    public void playVideo(View view){
        playVideo();
    }

    private void playVideo() {
        setPlaying(!this.isPlaying);
        if (this.isPlaying){
            if (isPrepared)
                mediaPlayer.start();
        }else{
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }
    }

    private void setPlaying(boolean isPlaying){
        if (this.isPlaying != isPlaying){
            this.isPlaying = isPlaying;
            btnPlay.setText(this.isPlaying?"pause":"play");
        }
    }

    private Uri getVideoUri(){
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        Uri videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        return videoUri;
    }

    @Override
    protected void onPause() {
        super.onPause();
        position = mediaPlayer.getCurrentPosition();
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("debug", "surfaceCreated "+position);
        mediaPlayer.setSurface(surfaceViewVideo.getHolder().getSurface());
        if (position > 0) {
            try {
                prepareVideo();
            } catch (IOException e) {
                e.printStackTrace();
            }
            playVideo();
            mediaPlayer.seekTo(position);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        if (isPlaying){
            playVideo();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setPlaying(!isPlaying);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    //    private void testVideoView(){
//        videoView = findViewById(R.id.video_view);
//        mediaController = new MediaController(this);
//
//        videoView.setMediaController(mediaController);
//        mediaController.setMediaPlayer(videoView);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
//                while(cursor.moveToNext()){
//                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
//                    Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
//                    Uri videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
//                    videoView.setVideoURI(videoUri);
//                    videoView.start();
//                    return;
//                }
//            }
//        }).start();
//    }

}
