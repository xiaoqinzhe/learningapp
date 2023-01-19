package com.example.learningapp.livedata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.learningapp.HHHHH;
import com.example.learningapp.AppApplication;
import com.example.learningapp.R;
import com.example.learningapp.data.objects.User;
import com.example.learningapp.data.viewmodels.UserProfileViewModel;

public class UserProfileActivity extends AppCompatActivity {

    UserProfileViewModel userProfileViewModel;
    TextView tvUserId;
    TextView tvUserName;
    TextView tvUserSign;
    // Data should be saved in ViewModel!!!
//    MutableLiveData<User> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        userProfileViewModel.init();
//        getWindow().addOnFrameMetricsAvailableListener();
        userProfileViewModel.getUserProfileById(userProfileViewModel.getUserId())
                // LifeCycle manages livedata (having activity reference) cycle
                .observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if(user == null){
                            Log.i("userProfile", "onChanged: user now is null");
                        }else{
                            Log.i("userProfile", "onChanged: user not null");
                            updateViewWithUserProfile(user);
                        }
                }
        });
        setContentView(R.layout.activity_user_profile);
        tvUserId = findViewById(R.id.tv_livedata_userprofile_id);
        tvUserName = findViewById(R.id.tv_livedata_userprofile_username);
        tvUserSign = findViewById(R.id.tv_livedata_userprofile_sign);
    }

    private void updateViewWithUserProfile(User user){
        tvUserId.setText(user.userId);
        tvUserName.setText(user.userName);
        tvUserSign.setText(user.userSign);
    }

    public void updateUserProfile(View view) {
//        userProfileViewModel.updateUserProfileFromWeb(userProfileViewModel.getUserId(), user);
        int a = HHHHH.a;
    }

    /**
     * 当activity被kill时，需要用到。（ViewModel仅在界面配置变化时起作用）
     * @param outState 存储少量界面信息
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
