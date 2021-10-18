package com.example.files.util;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

import com.example.files.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static void hideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();

        if (currentFocus == null) {
            currentFocus = new View(activity);
        }

        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    /**
     * @return the date time string in local format.
     */
    public static String formatDateTime(long timeMillis) {
        return SimpleDateFormat.getDateTimeInstance().format(new Date(timeMillis));
    }

    public static String getFileSize(long size) {
        /*
         * https://stackoverflow.com/a/18099948/7064179
         */
        if (size <= 0) {
            return "0";
        }

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void triggerNotification(Context context, int id, String content, @DrawableRes int icon,
                                           PendingIntent contentIntent, String channelId, String channelName) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

        builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(content)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setAutoCancel(true);

        builder.setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id, builder.build());
    }
}