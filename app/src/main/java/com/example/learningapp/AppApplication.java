package com.example.learningapp;

import android.app.Application;

import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;

import com.example.learningapp.kotlin.coroutine.TestApi;
import com.example.learningapp.views.emoji.EmojiTransferManager;

public class AppApplication extends Application {

    private static AppApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        TestApi.INSTANCE.start();
//        appDatabase = AppDatabase.getInstance(getApplicationContext());

        // Emoji Compat  download  （版本问题怎么办？）
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            FontRequest request = new FontRequest(
//                "com.example.learningapp",
//                    "com.example",
//                    "emoji compat Font Query",
//                R.array.certs
//            );
//            FontRequestEmojiCompatConfig config = new FontRequestEmojiCompatConfig(getApplicationContext(), request);
//            config.setReplaceAll(true);
//            config.setEmojiSpanIndicatorEnabled(false);
//
//            EmojiCompat.init(config);
//        }

        // 捆版
        BundledEmojiCompatConfig config = new BundledEmojiCompatConfig(getApplicationContext());
        EmojiCompat.init(config);

        // Emoji
        EmojiTransferManager.init(this);

    }

    public static AppApplication getApp() {
        return mApp;
    }
}
