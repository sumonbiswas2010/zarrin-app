package com.example.zarrin_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ApiResponseViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApiResponseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view); // Assuming the activity layout is defined correctly

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        adapter = new ApiResponseAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadApiResponses();
    }

    private void loadApiResponses() {
        new AsyncTask<Void, Void, List<ApiResponseEntity>>() {
            @Override
            protected List<ApiResponseEntity> doInBackground(Void... voids) {
                // Get the ApiResponseEntities from the database
                return ApiResponseDatabase.getInstance(getApplicationContext())
                        .apiResponseDao().getAllApiResponses();
            }

            @Override
            protected void onPostExecute(List<ApiResponseEntity> apiResponses) {
                if (apiResponses != null && !apiResponses.isEmpty()) {
                    adapter.updateData(apiResponses);
                } else {
                    Toast.makeText(ApiResponseViewActivity.this, "No API responses found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
