package com.abada.nstnote;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.abada.nstnote.UI.OnFLy;
import com.abada.nstnote.Utilities.Tools;

public class TileService extends android.service.quicksettings.TileService {
    public static boolean showed = false;
    private WindowManager wm;

    @Override
    public void onCreate() {
        if (Tools.getIns() == null)
            Tools.createIns(this);
        try {
            wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick() {
        showed = true;
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "You should give me the permission", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLocked())
            unlockAndRun(this::showOnFLyNote);
        else
            showOnFLyNote();
    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.drawable.tile_ic));
        tile.setLabel(getString(R.string.tile_label));
        tile.setState(Tools.getIns().isKept() && Settings.canDrawOverlays(this) ?
                Tile.STATE_ACTIVE
                : Tile.STATE_INACTIVE);
        if (showed)
            tile.setState(Tile.STATE_UNAVAILABLE);
        tile.updateTile();
    }

    private void showOnFLyNote() {
        if (wm == null) {
            Toast.makeText(this, "wm is null", Toast.LENGTH_LONG).show();
            return;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.RGBA_8888);
        params.windowAnimations = android.R.style.Animation_Translucent;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        OnFLy o = new OnFLy(inflater, getApplication()) {
            @Override
            public void close() {
                wm.removeView(this);
                showed = false;
            }
        };
        try {
            wm.addView(o, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
