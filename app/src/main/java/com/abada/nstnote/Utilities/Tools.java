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
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.R;
import com.abada.nstnote.databinding.PopupLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Tools {
    private final static MutableLiveData<Integer> counter = new MutableLiveData<>(0);
    private final static Map<Long, Boolean> isChecked = new HashMap<>();
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static SharedPreferences pref;

    public static void setContext(Context context) {
        if (Tools.context != null)
            return;
        Tools.context = context;
        pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    public static void copy(final Note note) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("" + note.header, "" + note.toString());
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public static void askDialog(Context context, View.OnClickListener yes, View.OnClickListener no) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        PopupLayoutBinding binding = PopupLayoutBinding.inflate(inflater, null, false);
        binding.body.setEnabled(false);
        binding.body.setText("Are you sure?");
        binding.body.setTextColor(context.getResources().getColor(R.color.fontColor, null));
        binding.save.setText("Yes");
        binding.cancel.setText("No");

        builder.setView(binding.getRoot());
        Dialog d = builder.create();
        binding.save.setOnClickListener(v1 -> {
            if (yes != null)
                yes.onClick(v1);
            d.dismiss();
        });
        binding.cancel.setOnClickListener(v1 -> {
            if (no != null)
                no.onClick(v1);
            d.dismiss();
        });
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();
    }

    public static String getCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return sm.format(currentTime);
    }

    public static MutableLiveData<Integer> getCounter() {
        return counter;
    }

    public static void checkNote(Long id) {
        if (isChecked.get(id) == null)
            isChecked.put(id, true);
        else
            isChecked.remove(id);
        int counter = getCounter().getValue() + (isChecked.get(id) != null ? 1 : -1);
        getCounter().setValue(counter);
        Log.i(TAG, "checkNote: " + counter);
    }

    public static boolean isChecked(Long id) {
        return Boolean.TRUE.equals(Tools.isChecked.get(id));
    }

    public static void keepNote(Long id) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = pref.edit();
        edit.putString("kept", id.toString());
        edit.apply();
    }

    public static Long getNoteId() {
        String out = pref.getString("kept", "0");
        keepNote(0L);
        if (out == null)
            return 0L;
        return Long.parseLong(out);
    }

    public static boolean isKept() {
        return !pref.getString("kept", "0").equals("0");
    }
}
