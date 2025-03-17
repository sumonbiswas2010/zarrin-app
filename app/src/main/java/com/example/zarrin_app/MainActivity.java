package com.example.zarrin_app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.zarrin_app.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Check if notification listener permission is granted
        if (!isNotificationServiceEnabled()) {
            requestNotificationPermission();
        } else {
            startNotificationListenerService();
        }
        Button btnViewNotifications = findViewById(R.id.btn_view_notifications);
        btnViewNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Check if the Notification Listener Service is enabled.
     */
    private boolean isNotificationServiceEnabled() {
        String packageName = getPackageName();
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners");

        return enabledListeners != null && enabledListeners.contains(packageName);
    }

    /**
     * Open notification access settings to allow user to grant permission.
     */
    private void requestNotificationPermission() {
        Toast.makeText(this, "Enable notification access for this app.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }

    /**
     * Start the Notification Listener Service.
     */
    private void startNotificationListenerService() {
        ComponentName componentName = new ComponentName(this, MyNotificationListenerService.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );

        // Start service explicitly
        Intent serviceIntent = new Intent(this, MyNotificationListenerService.class);
        startService(serviceIntent);

        Log.d(TAG, "NotificationListenerService started.");
    }
}
