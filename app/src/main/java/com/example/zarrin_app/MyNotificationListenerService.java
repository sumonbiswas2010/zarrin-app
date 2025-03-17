package com.example.zarrin_app;

import android.app.Notification;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.example.zarrin_app.NotificationDatabase;
import com.example.zarrin_app.NotificationEntity;

public class MyNotificationListenerService extends NotificationListenerService {

    private static final String TAG = "NotificationService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) return;

        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();
        CharSequence title = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence text = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
        long timestamp = System.currentTimeMillis();

        Log.d(TAG, "Received notification: " + title + " - " + text);

        // Store notification in database
        saveNotification(getApplicationContext(), packageName, title != null ? title.toString() : "", text != null ? text.toString() : "", timestamp);

        // Dismiss the notification
        cancelNotification(sbn.getKey());
    }

    private void saveNotification(Context context, String packageName, String title, String text, long timestamp) {
        NotificationEntity notification = new NotificationEntity(packageName, title, text, timestamp);
        new SaveNotificationTask(context).execute(notification);
    }

    private static class SaveNotificationTask extends AsyncTask<NotificationEntity, Void, Void> {
        private final Context context;

        SaveNotificationTask(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(NotificationEntity... notifications) {
            NotificationDatabase.getInstance(context).notificationDao().insertNotification(notifications[0]);
            return null;
        }
    }
}
