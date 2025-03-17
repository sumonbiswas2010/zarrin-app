package com.example.zarrin_app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String packageName;
    public String title;
    public String text;
    public long timestamp;

    public NotificationEntity(String packageName, String title, String text, long timestamp) {
        this.packageName = packageName;
        this.title = title;
        this.text = text;
        this.timestamp = timestamp;
    }
}
