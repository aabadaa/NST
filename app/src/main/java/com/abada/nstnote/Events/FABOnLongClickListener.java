package com.abada.nstnote.Events;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abada.nstnote.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FABOnLongClickListener implements View.OnLongClickListener {
    private final Context context;
    private final View.OnClickListener selectAll;
    private final View.OnClickListener delete;

    public FABOnLongClickListener(Context context, View.OnClickListener selectAll, View.OnClickListener delete) {
        this.context = context;
        this.selectAll = selectAll;
        this.delete = delete;
    }

    @Override
    public boolean onLongClick(View v) {
        FloatingActionButton button = (FloatingActionButton) v;
        Log.i("TAG", "onLongClick: " + v + " " + button);
        if ((boolean) button.getTag()) {
            button.setOnClickListener(selectAll);
            button.setImageResource(R.drawable.select_all_ic);
            Toast.makeText(context, "Select all", Toast.LENGTH_SHORT).show();
            button.setTag(false);
        } else {
            button.setOnClickListener(delete);
            button.setImageResource(R.drawable.delete_ic);
            Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
            button.setTag(true);
        }

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        return true;
    }
}
