package com.abada.nstnote;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;

public class Tools {
    public static AlertDialog AskOption(final Context context, final NoteAdapter arrayAdapter, final int position)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(new ContextThemeWrapper(context,R.style.Dialog))
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.delete_icon)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Note note = arrayAdapter.getItem(position);
                        IOManager iom = IOManager.getInstance(((MainActivity) context).getApplication());
                        iom.deleteNote(note);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }
    public static void copy(final Context context,final Note note){
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("" + note.getHeader(), "" + note.toString());
        clipboard.setPrimaryClip(clip);
    }
}
