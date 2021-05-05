package com.abada.nstnote.Utilities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Tools {
    @SuppressLint("StaticFieldLeak")
    private static Tools ins;
    private final Context context;
    private final MutableLiveData<Integer> counter;

    private Tools(Context context) {
        this.context = context;
        counter = new MutableLiveData<>();
    }

    public static void createIns(Context context) {
        if (ins == null)
            ins = new Tools(context);
    }

    public static Tools getIns() {
        return ins;
    }

    public void copy(final Note note) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("" + note.header, "" + note.toString());
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public void askDialog(View.OnClickListener yes, View.OnClickListener no) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);
        EditText text = v.findViewById(R.id.note);
        Button yesButton = v.findViewById(R.id.save);
        Button noButton = v.findViewById(R.id.cancel);
        text.setEnabled(false);
        text.setText("Are you sure?");
        text.setTextColor(context.getResources().getColor(R.color.fontColor, null));
        yesButton.setText("Yes");
        noButton.setText("No");

        builder.setView(v);
        Dialog d = builder.create();
        yesButton.setOnClickListener(v1 -> {
            if (yes != null)
                yes.onClick(v1);
            d.dismiss();
        });
        noButton.setOnClickListener(v1 -> {
            if (no != null)
                no.onClick(v1);
            d.dismiss();
        });
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();
    }

    public String getCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return sm.format(currentTime);
    }

    public MutableLiveData<Integer> getCounter() {
        return counter;
    }

    public void checkOne(boolean isChecked) {
        String c = "counter";
        SharedPreferences pref = context.getSharedPreferences(c, Context.MODE_PRIVATE);
        int newValue = pref.getInt(c, 0) + (isChecked ? 1 : -1);
        Log.i(TAG, "checkOne: " + newValue);
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor edit = pref.edit();
        counter.postValue(newValue);
        edit.putInt(c, newValue);
        edit.apply();
        Log.i(TAG, "checkOne: after edit " + pref.getInt(c, 0));
    }
}
