package com.example.zarrin_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApiResponseAdapter extends RecyclerView.Adapter<ApiResponseAdapter.ApiResponseViewHolder> {

    private List<ApiResponseEntity> apiResponseList;

    public ApiResponseAdapter(List<ApiResponseEntity> apiResponseList) {
        this.apiResponseList = apiResponseList;
    }

    @NonNull
    @Override
    public ApiResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.activity_notification_view, parent, false);
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_api_response, parent, false);  // Change if needed

        return new ApiResponseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ApiResponseViewHolder holder, int position) {
        ApiResponseEntity apiResponseEntity = apiResponseList.get(position);

        // Bind the data to the views
        holder.summaryTextView.setText(apiResponseEntity.summary);
        holder.alertsTextView.setText(apiResponseEntity.alerts);
        holder.tasksTextView.setText(apiResponseEntity.tasks);
        holder.calendarEventsTextView.setText(apiResponseEntity.calendar_events);
    }

    @Override
    public int getItemCount() {
        return apiResponseList != null ? apiResponseList.size() : 0;
    }

    // ViewHolder class
    public static class ApiResponseViewHolder extends RecyclerView.ViewHolder {
        TextView summaryTextView;
        TextView alertsTextView;
        TextView tasksTextView;
        TextView calendarEventsTextView;

        public ApiResponseViewHolder(View itemView) {
            super(itemView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            alertsTextView = itemView.findViewById(R.id.alertsTextView);
            tasksTextView = itemView.findViewById(R.id.tasksTextView);
            calendarEventsTextView = itemView.findViewById(R.id.calendarEventsTextView);
        }
    }

    // Method to update data in the adapter
    public void updateData(List<ApiResponseEntity> apiResponseList) {
        this.apiResponseList = apiResponseList;
        notifyDataSetChanged();
    }
}
