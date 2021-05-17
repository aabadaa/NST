package com.abada.nstnote.Utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.abada.nstnote.Note;
import com.abada.nstnote.Repositories.IOManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BackupManager {
    private final Context context;
    private final IOManager iom;

    public BackupManager(Context context, IOManager iom) {
        this.context = context;
        this.iom = iom;
    }

    public void backUp() {
        Log.i(TAG, "backUp: ");
        try {
            File f = context.getExternalFilesDir("NST backups");
            assert f != null;
            if (!f.exists()) {
                boolean isDirectoryCreated = f.mkdirs();
                if (!isDirectoryCreated)
                    Log.i("ATG", "Can't create directory to save the image");
                return;
            }
            Log.i(TAG, "backUp: " + f.getAbsoluteFile());
            String fileName = f.getPath() + File.separator + Tools.getCurrentDate() + ".nsb";
            ObjectOutputStream backupStream = new ObjectOutputStream(new FileOutputStream(fileName));
            //todo need to observe
            backupStream.writeObject(iom.getNotes().getValue());
            backupStream.close();
            Toast.makeText(context, "Backup is done", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void restore() {
        File f = context.getExternalFilesDir("NST backups");
        Log.i(TAG, "restore: " + f.getAbsoluteFile());
        if (f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files.length == 0) {
                Toast.makeText(context, "No backups found", Toast.LENGTH_LONG).show();
                return;
            }
            Arrays.sort(files);
            Log.i(TAG, "restore: " + files[0].getAbsoluteFile());
            File restoreFile = files[files.length - 1];
            try (
                    ObjectInputStream input = new ObjectInputStream(new FileInputStream(restoreFile))
            ) {
                List<Note> notes = (List<Note>) input.readObject();
                Note[] notesArray = new Note[notes.size()];
                notes.toArray(notesArray);
                Log.i(TAG, "restore: " + notes);
                iom.deleteAll();
                iom.insert(notesArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "Restore is done", Toast.LENGTH_SHORT).show();
        }
    }
}
