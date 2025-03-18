package com.example.zarrin_app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "api_responses")
public class ApiResponseEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String summary;
    public String alerts;           // Store as String, assuming alerts are strings.
    public String tasks;            // Store as String, assuming tasks are strings.
    public String calendar_events;  // Store as String, assuming calendar events are strings.

    // Constructor
    public ApiResponseEntity(String summary, String alerts, String tasks, String calendar_events) {
        this.summary = summary;
        this.alerts = alerts;
        this.tasks = tasks;
        this.calendar_events = calendar_events;
    }

    // Optionally, you can add getters and setters, but they're not required if you're using public fields.
}
