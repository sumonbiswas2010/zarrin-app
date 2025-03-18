package com.example.zarrin_app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(List.of()); // Avoid passing null
        recyclerView.setAdapter(adapter);

        loadNotifications();

        Button sendNotificationsButton = findViewById(R.id.btnSendNotifications);
        sendNotificationsButton.setOnClickListener(v -> sendNotificationsToBackend());

        // Inside onCreate()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR
            }, 100);
        }
    }

    private void showResponseModal(String summary) {
        new AlertDialog.Builder(this)
                .setTitle("New Notification Summary")
                .setMessage(summary)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveApiResponse(ApiResponse apiResponse) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
//                NotificationDatabase db = NotificationDatabase.getInstance(getApplicationContext());
                ApiResponseDatabase apiResponseDatabase = ApiResponseDatabase.getInstance(getApplicationContext());
                long timestamp = System.currentTimeMillis();

                // Save notification data

                // Save API response summary into ApiResponseDatabase
                if (apiResponse.data != null) {
                    // Convert lists to JSON strings if necessary
                    String alertsJson = new Gson().toJson(apiResponse.data.alerts);
                    String tasksJson = new Gson().toJson(apiResponse.data.tasks);
                    String calendarEventsJson = new Gson().toJson(apiResponse.data.calender_events);

                    // Create an ApiResponseEntity and insert it into the database
                    ApiResponseEntity apiResponseEntity = new ApiResponseEntity(
                            apiResponse.data.summary,
                            alertsJson,  // Store list as JSON string
                            tasksJson,   // Store list as JSON string
                            calendarEventsJson  // Store list as JSON string
                    );
                    apiResponseDatabase.apiResponseDao().insert(apiResponseEntity);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                loadNotifications(); // Refresh RecyclerView with new data
            }
        }.execute();
    }

    private void sendNotificationsToBackend() {
        // Get the current list of notifications
        List<NotificationEntity> notifications = adapter.getNotifications();

        // Create the Retrofit API client
        NotificationApi api = RetrofitClient.getClient().create(NotificationApi.class);

        // Send the notifications to the backend
        Call<ApiResponse> call = api.sendNotifications(notifications);

        // Enqueue the request to run asynchronously
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    // Save response data in local database
                    saveApiResponse(apiResponse);

                    if (apiResponse.data.calender_events != null) {
                        addEventsToGoogleCalendar(apiResponse.data.calender_events);
                    }

                    // Show modal dialog with response summary
                    showResponseModal(apiResponse.data.summary);

                    Toast.makeText(NotificationActivity.this, "Response received & saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addEventsToGoogleCalendar(List<String> icsList) {
        for (String ics : icsList) {
            String title = extractValue(ics, "SUMMARY:");
            String dtStart = extractValue(ics, "DTSTART;");
            String dtEnd = extractValue(ics, "DTEND;");

            // Extract timezone if present (e.g., "TZID=Asia/Dhaka:")
            String timezone = extractTimezone(dtStart);

            // Clean the datetime string to remove the timezone part (if any)
            String cleanedDtStart = dtStart.split(":")[1];
            String cleanedDtEnd = dtEnd.split(":")[1];

            long startMillis = convertToMillis(cleanedDtStart, timezone);
            long endMillis = convertToMillis(cleanedDtEnd, timezone);

            insertEventIntoCalendar(title, startMillis, endMillis);
        }
    }

    private String extractTimezone(String dtStart) {
        if (dtStart.contains("TZID=")) {
            return dtStart.split("TZID=")[1].split(":")[0]; // Extract the timezone from DTSTART
        }
        return "UTC"; // Default if no timezone is present
    }

    private long convertToMillis(String dateTime, String timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(timezone)); // Set the timezone to handle conversion
        try {
            Date date = sdf.parse(dateTime);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Calendar permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Calendar permission denied. Cannot add events.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void insertEventIntoCalendar(String title, long startMillis, long endMillis) {
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // Default calendar
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC");

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(this, "Event added to Google Calendar", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
        }
    }

    private String extractValue(String ics, String key) {
        for (String line : ics.split("\n")) {
            if (line.startsWith(key)) {
                return line.replace(key, "").trim();
            }
        }
        return "";
    }

    private long convertToMillis(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
        try {
            Date date = sdf.parse(dateTime);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    private void loadNotifications() {
        new AsyncTask<Void, Void, List<NotificationEntity>>() {
            @Override
            protected List<NotificationEntity> doInBackground(Void... voids) {
                return NotificationDatabase.getInstance(getApplicationContext()).notificationDao().getAllNotifications();
            }

            @Override
            protected void onPostExecute(List<NotificationEntity> notifications) {
                adapter.updateData(notifications);
            }
        }.execute();
    }
}
