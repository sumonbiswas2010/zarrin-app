//package com.example.zarrin_app;
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.gson.Gson;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ApiResponseViewActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private NotificationAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notification_view); // Create the XML layout for this Activity
//
//        recyclerView = findViewById(R.id.recyclerViewNotifications);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // Initialize the adapter with an empty list
//        adapter = new NotificationAdapter(new ArrayList<>());
//        recyclerView.setAdapter(adapter);
//
//        loadApiResponses();
//    }
//
//    private void loadApiResponses() {
//        new AsyncTask<Void, Void, List<ApiResponseEntity>>() {
//            @Override
//            protected List<ApiResponseEntity> doInBackground(Void... voids) {
//                // Get the ApiResponseEntities from the database
//                List<ApiResponseEntity> apiResponseEntities = ApiResponseDatabase.getInstance(getApplicationContext())
//                        .apiResponseDao().getAllApiResponses();
//
//                List<ApiResponseEntity> apiResponses = new ArrayList<>();
//
//                if (apiResponseEntities != null) {
//                    Gson gson = new Gson();
//                    for (ApiResponseEntity entity : apiResponseEntities) {
//                        // Parse JSON data from the database
//                        List<String> alerts = gson.fromJson(entity.alerts, List.class);
//                        // We only process alerts here as per your requirement
//                        apiResponses.addAll(createApiResponseData(alerts, entity));
//                    }
//                }
//
//                return apiResponses;
//            }
//
//            @Override
//            protected void onPostExecute(List<ApiResponseEntity> apiResponses) {
//                if (apiResponses != null && !apiResponses.isEmpty()) {
//                    adapter.updateData(apiResponses); // Update the RecyclerView with the data
//                } else {
//                    Toast.makeText(ApiResponseViewActivity.this, "No API responses found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }.execute();
//    }
//
//    // Helper method to create ApiResponseEntity data
//    private List<ApiResponseEntity> createApiResponseData(List<String> dataList, ApiResponseEntity originalEntity) {
//        List<ApiResponseEntity> apiResponses = new ArrayList<>();
//        if (dataList != null) {
//            for (String data : dataList) {
//                // Create a new ApiResponseEntity for each alert (using the existing fields from originalEntity)
//                apiResponses.add(new ApiResponseEntity(originalEntity.summary, data, "", ""));
//            }
//        }
//        return apiResponses;
//    }
//}
