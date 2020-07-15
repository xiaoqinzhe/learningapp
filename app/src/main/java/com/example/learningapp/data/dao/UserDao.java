package com.example.learningapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.learningapp.data.objects.User;

@Dao
public interface UserDao {

    @Query(value = "select * from users where id = :userId")
    public User getUserById(String userId);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public long insertUser(User user);

    @Update
    public int updateUser(User user);

    @Delete
    public int deleteUser(User user);

}
