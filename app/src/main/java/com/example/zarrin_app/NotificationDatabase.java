package com.example.zarrin_app;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NotificationEntity.class, ApiResponseEntity.class}, version = 1, exportSchema = false)
public abstract class NotificationDatabase extends RoomDatabase {

    private static volatile NotificationDatabase INSTANCE;

    public abstract NotificationDao notificationDao();
    public abstract ApiResponseDao apiResponseDao();

    public static NotificationDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NotificationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    NotificationDatabase.class, "notification_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
