package com.example.zarrin_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view); // Create the XML layout for this Activity

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        adapter = new NotificationAdapter(null);
        recyclerView.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        new AsyncTask<Void, Void, List<NotificationEntity>>() {
            @Override
            protected List<NotificationEntity> doInBackground(Void... voids) {
                return NotificationDatabase.getInstance(getApplicationContext()).notificationDao().getAllNotifications();
            }

            @Override
            protected void onPostExecute(List<NotificationEntity> notifications) {
                if (notifications != null) {
                    adapter.updateData(notifications);
                } else {
                    Toast.makeText(NotificationViewActivity.this, "No notifications found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
