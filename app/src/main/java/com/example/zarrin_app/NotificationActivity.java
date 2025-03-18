package com.example.zarrin_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

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
                NotificationDatabase db = NotificationDatabase.getInstance(getApplicationContext());
                ApiResponseDatabase apiResponseDatabase = ApiResponseDatabase.getInstance(getApplicationContext());
                long timestamp = System.currentTimeMillis();

                // Save notification data
                if (apiResponse.data != null) {
                    db.notificationDao().insertNotification(new NotificationEntity(apiResponse.data.summary, "summary", apiResponse.data.summary, timestamp));

                    if (apiResponse.data.alerts != null) {
                        for (String alert : apiResponse.data.alerts) {
                            db.notificationDao().insertNotification(new NotificationEntity(alert, "alert", alert, timestamp));
                        }
                    }

                    if (apiResponse.data.tasks != null) {
                        for (String task : apiResponse.data.tasks) {
                            db.notificationDao().insertNotification(new NotificationEntity(task, "task", task, timestamp));
                        }
                    }

                    if (apiResponse.data.calender_events != null) {
                        for (String event : apiResponse.data.calender_events) {
                            db.notificationDao().insertNotification(new NotificationEntity(event, "calendar", event, timestamp));
                        }
                    }
                }

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
