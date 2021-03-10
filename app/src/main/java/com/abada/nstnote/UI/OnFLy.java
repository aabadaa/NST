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

import androidx.lifecycle.ViewModelProvider;

import com.abada.nstnote.Note;
import com.abada.nstnote.R;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.ViewModels.SingleNoteModel;

public abstract class OnFLy extends FrameLayout {
    //Views
    private final View v;
    private final Button save;
    private final Button cancel;
    private final EditText body;
    private final IOManager iom;
    //others
    private final Application application;
    //Models
    SingleNoteModel viewModel;
    //data
    private Note newNote;
    private String toastText = "Saved";

    public OnFLy(Application application) {
        super(application);
        this.application = application;
        this.v = LayoutInflater.from(application).inflate(R.layout.activity_popup, this);
        iom = IOManager.getInstance(application);
        save = v.findViewById(R.id.save);
        body = v.findViewById(R.id.note);
        cancel = v.findViewById(R.id.cancel);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(SingleNoteModel.class);
        viewModel.getNote().observeForever(note -> body.setText(note.getBody()));
        save.setOnClickListener(v1 -> {
            save();
            doClose();
        });
        cancel.setOnClickListener(v1 -> {
            toastText = "Canceled";
            viewModel.getNote().setValue(new Note());
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
                viewModel.getNote().setValue(new Note(body.getText().toString()));
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
        viewModel.getNote().setValue(newNote);
        if (newNote.getBody().isEmpty()) {
            toastText = "Canceled";
        } else {
            viewModel.edit(SingleNoteModel.INSERT);
        }
    }

    private void showHideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        body.requestFocus();
    }

}
