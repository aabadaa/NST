package com.abada.nstnote;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class Tools {

    public static void copy(final Context context,final Note note){
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("" + note.getHeader(), "" + note.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }
}
