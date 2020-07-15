package com.example.learningapp.data.net;

import com.example.learningapp.data.objects.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("user")
    Call<User> getUser(@Query("userId")String userId);
}
