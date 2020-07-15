package com.example.learningapp.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.learningapp.data.dao.UserDao;
import com.example.learningapp.data.objects.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_FILENAME = "learningapp.db";

    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_FILENAME)
//                    .allowMainThreadQueries()
                    .allowMainThreadQueries()
                    .addMigrations()
                    .build();
    }

    // 迁移
    private static final Migration migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

}
