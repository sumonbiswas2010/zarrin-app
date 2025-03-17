package com.example.zarrin_app;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NotificationEntity.class}, version = 1)
public abstract class NotificationDatabase extends RoomDatabase {
    public abstract NotificationDao notificationDao();

    private static volatile NotificationDatabase INSTANCE;

    public static NotificationDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NotificationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    NotificationDatabase.class, "notification_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
