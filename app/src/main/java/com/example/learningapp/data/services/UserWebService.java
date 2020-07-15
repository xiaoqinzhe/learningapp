package com.example.learningapp.data.services;

import com.example.learningapp.data.objects.User;

public class UserWebService {

    public User getUserById(String userId){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.userId = userId;
        user.userName = "xiaoqinzhe";
        user.userSign = "Stay hungry,stay foolish.";
        return user;
    }

}
