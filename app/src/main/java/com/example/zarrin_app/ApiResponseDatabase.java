package com.example.zarrin_app;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.zarrin_app.ApiResponseDao;
import com.example.zarrin_app.ApiResponseEntity;

@Database(entities = {ApiResponseEntity.class}, version = 1)
public abstract class ApiResponseDatabase extends RoomDatabase {
    private static volatile ApiResponseDatabase INSTANCE;

    public abstract ApiResponseDao apiResponseDao();

    public static ApiResponseDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ApiResponseDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ApiResponseDatabase.class, "api_response_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
