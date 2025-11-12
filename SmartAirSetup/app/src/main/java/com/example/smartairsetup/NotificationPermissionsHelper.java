package com.example.smartairsetup;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

public class NotificationPermissionsHelper {

    public static boolean ensureNotificationAndAlarmPermissions(Context context) {
        boolean ok = true;

        // Check notification permission
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (!manager.areNotificationsEnabled()) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);

            showPermissionDialog(activity, "exact_alarms");
            return false; // exit, user needs to grant permission first
           // ok = false;
        }

        // Check alarm permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
                ok = false;
            }
        }

        return ok;
    }

    private static void showPermissionDialog(Activity activity, String type) {
        String message;
        if (type.equals("exact_alarms")) {
            message = "This app needs exact alarm permission to send timely medication reminders.";
        } else {
            message = "This app needs notification permission to alert you about medications.";
        }

        new AlertDialog.Builder(activity)
                .setTitle("Permission Needed")
                .setMessage(message)
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent;
                    if (type.equals("exact_alarms") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    } else {
                        intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
                    }
                    activity.startActivity(intent);
                })
                .setNegativeButton("Maybe Later", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }


}
