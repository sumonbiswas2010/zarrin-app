package com.example.zarrin_app;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    void insertNotification(NotificationEntity notification);

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    List<NotificationEntity> getAllNotifications();
}
