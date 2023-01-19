package com.example.learningapp.data.repository;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.learningapp.data.objects.User;
import com.example.learningapp.data.services.UserWebService;
import com.example.learningapp.utils.MyThreadPool;

import java.util.Timer;
import java.util.TimerTask;

public class UserRepository {
    private UserWebService userWebService;
    private MutableLiveData<User> userCache;   // TODO

    public UserRepository(){
        // TODO 依赖注入  (/服务定位器)
        userWebService = new UserWebService();
        userCache = new MutableLiveData<>();
        Log.i("UserRepository", "Constructor");
    }

    public MutableLiveData<User> getUserById(String userId){
        if(userCache.getValue() != null && userCache.getValue().userId.equals(userId))
            return userCache;
        // background: remote + local
        final String uid = userId;


//        final Handler handler = new Handler() {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                Log.i("UserRepository", "handle msg");
//                userCache.setValue((User)msg.getData().getParcelable("user"));
//            }
//        };
//        MyThreadPool.THREAD_POOL_EXECUTOR.execute(new Runnable() {
//            @Override
//            public void run() {
//                User u = userDao.getUserById(uid);
//                if(u == null){
//                    u = userWebService.getUserById(uid);
//                    userDao.updateUser(u);
//                }
//                Message msg = new Message();
//                msg.what = 233;
//                Bundle data = new Bundle();
//                data.putParcelable("user", u);
//                msg.setData(data);
//                handler.sendMessage(msg);
//            }
//        });

//        if(u == null){
//            refreshUser(userId, userCache);
//            Log.i("UserRepository", "user " + userId + " is not in local db");
//        }
//        else{
//            userCache.setValue(u);
//            Log.i("UserRepository", "user " + userId + " is in local db");
//        }
        return userCache;
    }

    public void refreshUser(String userId, final MutableLiveData<User> user){
        final String uid = userId;
        // async
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i("UserRepository", "handle msg");
                user.setValue((User)msg.getData().getParcelable("user"));
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                User user = userWebService.getUserById(uid);
                Message msg = Message.obtain();
                msg.what = 233;
                Bundle data = new Bundle();
                data.putString("userSign", user.userSign);
                data.putParcelable("user", user);
                msg.setData(data);
                Log.i("UserRepository", "send message");
                handler.sendMessage(msg);
            }
        }, 0);
    }

}
