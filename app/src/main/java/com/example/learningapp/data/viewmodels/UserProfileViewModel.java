package com.example.learningapp.data.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.learningapp.data.database.AppDatabase;
import com.example.learningapp.data.net.RetrofitService;
import com.example.learningapp.data.objects.User;
import com.example.learningapp.data.repository.UserRepository;
import com.example.learningapp.utils.L;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * ViewModel 保存UI数据，且不能引用Activity，View，Lifecycle 和其它上下文相关的！（防止内存泄漏，在配置改变时仍然存在）
 * Fragment 之间共享数据
 * 进程被杀无法恢复，需配合onSaveInstanceState使用！
 */
public class UserProfileViewModel extends ViewModel {
    private String userId;
    // 加载/获取/持久化应用App数据
    private UserRepository userRepository;
    private MutableLiveData<User> user;
//    // ；livedata通知链
//    private final MutableLiveData<String> addressInput = new MutableLiveData<>();
//    public final LiveData<String> postalCode =
//            Transformations.switchMap(addressInput, (address) -> {
//                return userRepository.getPostCode(address);
//            });

    // TODO DI
    public void init(){
        if(userRepository == null){
            userRepository = new UserRepository();
        }
    }

    public LiveData<User> getUserProfileById(String userId){
        this.userId = userId;
        user = userRepository.getUserById(userId);
        return user;
    }

    // 持久化 userId
    public String getUserId(){
        if(userId == null){
            // local  /  web
            userId = "xiaoqinzhe";
        }
        return userId;
    }

    public void updateUserProfileFromWeb(String userId) {
        userRepository.refreshUser(userId, user);
    }

    public void retrofitGetUser(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://xxx.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<User> call = retrofitService.getUser("id");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                L.d(response.body().userName);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
