package com.abada.nstnote;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

public class Tools {
    public static AlertDialog AskOption(final Context context) {
        return new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Dialog))
                .setTitle("Delete")
                .setMessage("Are you sure?")
                .setIcon(R.drawable.icondelete)
                .setPositiveButton("Yes", (dialog, whichButton) -> {
                    IOManager iom = IOManager.getInstance(((MainActivity) context).getApplication());
                    iom.deleteSelected();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create();
    }
    public static void copy(final Context context,final Note note){
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("" + note.getHeader(), "" + note.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }
}
