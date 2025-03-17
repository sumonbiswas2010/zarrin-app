package com.example.zarrin_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zarrin_app.R;
import com.example.zarrin_app.NotificationDatabase;
import com.example.zarrin_app.NotificationEntity;
import java.util.List;

import retrofit2.Call;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(null);
        recyclerView.setAdapter(adapter);

        loadNotifications();
        Button sendNotificationsButton = findViewById(R.id.btnSendNotifications);
        sendNotificationsButton.setOnClickListener(v -> {
            sendNotificationsToBackend();
        });



    }

    private void sendNotificationsToBackend() {
        // Get the current list of notifications
        List<NotificationEntity> notifications = adapter.getNotifications();

        // Create the Retrofit API client
        NotificationApi api = RetrofitClient.getClient().create(NotificationApi.class);

        // Send the notifications to the backend
        Call<Void> call = api.sendNotifications(notifications);

        // Enqueue the request to run asynchronously
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    // Successfully sent notifications
                    Toast.makeText(NotificationActivity.this, "Notifications sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    // Something went wrong
                    Toast.makeText(NotificationActivity.this, "Failed to send notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Network or other failure
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
