package com.abada.nstnote.UI;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abada.nstnote.IOManager;
import com.abada.nstnote.Note;
import com.abada.nstnote.R;

public class OnFLy {
    //Views
    Button save, cancel;
    EditText body;
    //data
    Note newNote;
    String toastText = "saved";
    IOManager iom;
    //events
    Closer closer;
    InputMethodManager inputMethodManager;
    View.OnKeyListener backButton;
    //others
    Application application;

    {
        backButton = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("TAG", "onKey: " + keyCode + " " + event);
                if (body.getText().toString().isEmpty())
                    toastText = "Canceled";
                else {
                    iom.getNote().setValue(new Note(body.getText().toString()));
                    toastText = "kept";
                }
                close();
                return true;
            }

        };
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

    public OnFLy(View v, Application application, Closer closer) {
        this.application = application;
        inputMethodManager = (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
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
            toastText = "Canceled";
            iom.getNote().setValue(new Note());
            close();
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(backButton);
        showKeyboard();
        body.setOnClickListener(v1 -> showKeyboard());
    }

    public interface Closer {
        void close();
    }

    void close() {
        Toast.makeText(application, toastText, Toast.LENGTH_SHORT).show();
        closer.close();
        inputMethodManager.hideSoftInputFromWindow(body.getWindowToken(), 0);
    }

    private void showKeyboard() {
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        body.requestFocus();
    }

}
