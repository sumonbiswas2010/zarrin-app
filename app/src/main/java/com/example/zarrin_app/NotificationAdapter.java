package com.example.zarrin_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zarrin_app.R;
import com.example.zarrin_app.NotificationEntity;
import java.util.List;
import java.util.ArrayList;  // Import ArrayList

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationEntity> notifications;

    // Constructor: Initialize the list to avoid NullPointerException
    public NotificationAdapter(List<NotificationEntity> notifications) {
        // If the list is null, initialize it as an empty list
        this.notifications = notifications != null ? notifications : new ArrayList<>();
    }

    // Method to update the data in the adapter
    public void updateData(List<NotificationEntity> newNotifications) {
        this.notifications = newNotifications != null ? newNotifications : new ArrayList<>();
        notifyDataSetChanged();
    }
    public List<NotificationEntity> getNotifications() {
        return notifications; // Return the current list of notifications
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Check if position is valid
        if (position >= 0 && position < notifications.size()) {
            NotificationEntity notification = notifications.get(position);
            holder.title.setText(notification.title);
            holder.text.setText(notification.text);
            holder.timestamp.setText(String.valueOf(notification.timestamp));
        }
    }

    @Override
    public int getItemCount() {
        // Safely return the size, ensuring it's never null
        return notifications != null ? notifications.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, text, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            text = itemView.findViewById(R.id.notification_text);
            timestamp = itemView.findViewById(R.id.notification_timestamp);
        }
    }
}
