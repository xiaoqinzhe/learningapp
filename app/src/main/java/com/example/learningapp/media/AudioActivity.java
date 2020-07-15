package com.example.learningapp.media;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.learningapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private List<AudioFileDesc> audioList;
    private int mCurAudioIndex;

    private Button mBtnPlay;
    private Button mBtnPrev;
    private Button mBtnNext;

    private MediaPlayer mMediaPlayer;
    private boolean isPlaying;
    private boolean isPrepared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setWakeMode(this, PowerManager.WAKE);

        initAudioList();

        try {
            audioList.get(mCurAudioIndex).setDataSource(mMediaPlayer);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPrepared = false;
        isPlaying = false;

        initViews();

        initEvents();

    }

    private void initEvents() {
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
    }

    public void playAudio(View v){
        setPlaying(!isPlaying);
        if (isPlaying && isPrepared){
            mMediaPlayer.start();
        }
        if (!isPlaying && mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    public void prevAudio(View view) {
        mMediaPlayer.reset();
        int last = mCurAudioIndex;
        mCurAudioIndex -= 1;
        if (mCurAudioIndex < 0)
            mCurAudioIndex = audioList.size()-1;
        try {
            if (mCurAudioIndex != last) {
                playNewAudio(audioList.get(mCurAudioIndex));
            }else{
                playAgain();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextAudio(View view) {
        int last = mCurAudioIndex;
        mCurAudioIndex += 1;
        if (mCurAudioIndex >= audioList.size())
            mCurAudioIndex = 0;
        try {
            if (mCurAudioIndex != last) {
                playNewAudio(audioList.get(mCurAudioIndex));
            }else{
                playAgain();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playNewAudio(AudioFileDesc audioFileDesc) throws IOException {
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        mMediaPlayer.reset();
        audioFileDesc.setDataSource(mMediaPlayer);
        mMediaPlayer.prepareAsync();
        isPrepared = false;
        setPlaying(true);
    }

    public void playAgain() throws IOException {
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        mMediaPlayer.prepareAsync();
        isPrepared = false;
        setPlaying(true);
    }

    public void setPlaying(boolean isPlaying){
        if (this.isPlaying != isPlaying) {
            this.isPlaying = isPlaying;
            mBtnPlay.setText(isPlaying ? "Pause" : "Play");
        }
    }

    private void initViews() {
        mBtnNext = findViewById(R.id.btn_audio_next);
        mBtnPlay = findViewById(R.id.btn_audio_play);
        mBtnPrev = findViewById(R.id.btn_audio_prev);

    }

    private void initAudioList(){
        audioList = new ArrayList<>();
        AudioFileDesc audio = new AudioFileDesc(this, R.raw.xiaoban);
        audioList.add(audio);
        try {
            audio = new AudioFileDesc(getAssets().openFd("songdongye.mp3"));
            audioList.add(audio);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurAudioIndex = 0;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextAudio(null);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (isPlaying)
            mMediaPlayer.start();
        isPrepared = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }
}


class AudioFileDesc{
    public static final int SOURCE_RAW = 0;
    public static final int SOURCE_ASSET = 1;
    public static final int SOURCE_FILE = 2;
    public static final int SOURCE_URI = 3;
    public static final int SOURCE_DRP = 4;

    public String name;


    public int audioType;

    private int resId;
    private AssetFileDescriptor afd;

    public AudioFileDesc(Context context, int resId){
        audioType = SOURCE_RAW;
        this.resId = resId;
        afd = context.getResources().openRawResourceFd(resId);
    }

    public AudioFileDesc(AssetFileDescriptor afd){
        audioType = SOURCE_ASSET;
        this.afd = afd;
    }

    public void setDataSource(MediaPlayer mediaPlayer) throws IOException {
        switch (audioType){
            case SOURCE_RAW:
            case SOURCE_ASSET:
                mediaPlayer.setDataSource(afd);
                break;
        }
    }



}
