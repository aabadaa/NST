package com.abada.nstnote.Utilities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abada.nstnote.Note;
import com.abada.nstnote.R;

import java.util.Objects;

public class Tools {

    public static void copy(final Context context, final Note note) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("" + note.header, "" + note.toString());
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public static void askDialog(final Context context, View.OnClickListener yes, View.OnClickListener no) {
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
}
