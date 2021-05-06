package com.abada.nstnote;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.abada.nstnote.UI.OnFLy;

import java.util.List;
import java.util.concurrent.Future;

public class TileService extends android.service.quicksettings.TileService {
    public static Future<List<Long>> lastNoteId;
    public static boolean showed = false;
    private WindowManager wm;

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public void onClick() {

        showed = true;
        Log.i("TAG", "onClick: " + lastNoteId);
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "You should give me the permission", Toast.LENGTH_SHORT).show();
            return;
        }
        showNotification();
        showOnFLyNote();

    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.drawable.tile_ic));
        tile.setLabel(getString(R.string.tile_label));
        tile.setState(lastNoteId != null && Settings.canDrawOverlays(this) ?
                Tile.STATE_ACTIVE
                : Tile.STATE_INACTIVE);
        if (showed || isLocked())
            tile.setState(Tile.STATE_UNAVAILABLE);
        tile.updateTile();
    }

    private void showOnFLyNote() {

        if (wm == null) {
            Toast.makeText(this, "wm is null", Toast.LENGTH_LONG).show();
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.RGBA_8888);
        params.windowAnimations = android.R.style.Animation_Translucent;
        OnFLy o = new OnFLy(inflater, getApplication()) {
            @Override
            public void close() {
                wm.removeView(this);
                showed = false;
            }
        };
        wm.addView(o, params);

    }

    private void showNotification() {
        Intent fullScreenIntent = new Intent(this, getClass());
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, createNotificationChannel("On fly note", "On fly note"))
                        .setSmallIcon(R.drawable.tile_ic)
                        .setContentTitle("On fly Note")
                        .setContentText("needed to show the dialog")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setFullScreenIntent(fullScreenPendingIntent, true);
        Notification no = notificationBuilder.build();
        startForeground(1999, no);

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }
}