package com.abada.nstnote;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;

public class TileService extends android.service.quicksettings.TileService {
    public static boolean clicked=false;
    @Override
    public void onClick() {
        clicked=true;
        startActivity(new Intent(this, OnFLyActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.drawable.ic_tile));
        tile.setLabel(getString(R.string.tile_label));

        tile.setState(clicked?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
