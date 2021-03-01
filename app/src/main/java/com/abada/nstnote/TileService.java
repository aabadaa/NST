package com.abada.nstnote;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.abada.nstnote.UI.OnFLy;

public class TileService extends android.service.quicksettings.TileService {
    public static boolean clicked = false;

    @Override
    public void onClick() {
        clicked = true;
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        int LAYOUT_FLAG;
        LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE
                ,
                PixelFormat.TRANSLUCENT);
        View v = li.inflate(R.layout.activity_popup, null);

        OnFLy o = new OnFLy(v, getApplication(), () -> {
            clicked = false;
            wm.removeView(v);
        });
        wm.addView(v, params);
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    @Override
    public void onStartListening() {
        Log.i("TAG", "onStartListening: ");
        Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.drawable.tile_ic));
        tile.setLabel(getString(R.string.tile_label));

        tile.setState(clicked ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
