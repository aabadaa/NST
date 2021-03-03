package com.abada.nstnote.UI;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.abada.nstnote.IOManager;
import com.abada.nstnote.Note;
import com.abada.nstnote.R;

public abstract class OnFLy extends FrameLayout {
    //Views
    private final View v;
    private final Button save;
    private final Button cancel;
    private final EditText body;
    private final IOManager iom;
    //events
    private final InputMethodManager inputMethodManager;
    //others
    private final Application application;
    //data
    private Note newNote;
    private String toastText = "Saved";

    public OnFLy(Application application) {
        super(application);
        this.application = application;
        this.v = LayoutInflater.from(application).inflate(R.layout.activity_popup, this);
        inputMethodManager = (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
        iom = IOManager.getInstance(application);
        save = v.findViewById(R.id.save);
        body = v.findViewById(R.id.note);
        cancel = v.findViewById(R.id.cancel);
        iom.getNote().observeForever(note -> body.setText(note.getBody()));
        save.setOnClickListener(v1 -> {
            save();
            doClose();
        });
        cancel.setOnClickListener(v1 -> {
            toastText = "Canceled";
            iom.getNote().setValue(new Note());
            doClose();
        });
        showHideKeyboard();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("?", "dispatchKeyEventPreIme: " + event);
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (body.getText().toString().isEmpty())
                toastText = "Canceled";
            else {
                iom.getNote().setValue(new Note(body.getText().toString()));
                toastText = "Kept";
            }
            doClose();
        }
        return super.dispatchKeyEvent(event);
    }

    public abstract void close();

    private void doClose() {
        Toast.makeText(application, toastText, Toast.LENGTH_SHORT).show();
        close();

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

    private void showHideKeyboard() {
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        body.requestFocus();
    }

}
