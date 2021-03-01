package com.abada.nstnote.UI;

import android.app.Application;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abada.nstnote.IOManager;
import com.abada.nstnote.Note;
import com.abada.nstnote.R;

public class OnFLy {
    Button save, cancel;
    EditText body;
    Note newNote;
    String toastText = "saved";
    IOManager iom;
    Closer closer;
    Application application;

    public OnFLy(View v, Application application, Closer closer) {
        this.application = application;
        this.closer = closer;
        iom = IOManager.getInstance(application);
        save = v.findViewById(R.id.save);
        body = v.findViewById(R.id.note);
        cancel = v.findViewById(R.id.cancel);
        iom.getNote().observeForever(note -> body.setText(note.getBody()));
        save.setOnClickListener(v1 -> {
            save();
            close();
        });
        cancel.setOnClickListener(v1 -> {
            toastText = "canceled";
            iom.getNote().setValue(new Note());
            close();
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener((v12, keyCode, event) -> {
            Log.i("TAG", "OnFLy: " + keyCode + " " + event);
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                if (body.getText().toString().isEmpty())
                    toastText = "Canceled";
                else {
                    iom.getNote().setValue(new Note(body.getText().toString()));
                    toastText = "kept";

                }
                close();
                return true;
            }
            return false;
        });
    }

    private void save() {
        toastText = "saved";
        String body = this.body.getText().toString();
        String header = body;
        if (header.contains("\n"))
            header = header.substring(0, header.indexOf("\n"));
        if (header.length() > 30)
            header = header.substring(30);
        newNote = new Note(header, body);
        if (newNote.getBody().isEmpty()) {
            toastText = "canceled";
        } else {
            iom.insert(newNote);
        }
        iom.getNote().setValue(new Note());
    }

    void close() {
        Toast.makeText(application, toastText, Toast.LENGTH_SHORT).show();
        closer.close();
    }

    public interface Closer {
        void close();
    }
}
