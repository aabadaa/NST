package com.abada.nstnote;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.abada.nstnote.UI.Activities.OnFLyActivity;
import com.abada.nstnote.UI.OnFLy;

import java.util.concurrent.Future;

public class TileService extends android.service.quicksettings.TileService {
    public static Future<Long> lastNoteId;
    public static boolean showed = false;

    @Override
    public void onClick() {
        showed = true;
        Log.i("TAG", "onClick: " + lastNoteId);
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "You should give me the permission", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLocked())
            startActivity(new Intent(this, OnFLyActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        else
            showOnFLyNote();
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
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
        if (showed)
            tile.setState(Tile.STATE_UNAVAILABLE);
        tile.updateTile();
    }

    private void showOnFLyNote() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.RGBA_8888);
        params.windowAnimations = android.R.style.Animation_Translucent;
        OnFLy o = new OnFLy(getApplication()) {
            @Override
            public void close() {
                wm.removeView(this);
                showed = false;
            }
        };
        wm.addView(o, params);
    }
}
