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
import com.abada.nstnote.TileService;
import com.abada.nstnote.Utilities.State;
import com.abada.nstnote.ViewModels.SingleNoteViewModel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    SingleNoteViewModel viewModel;
    private String toastText = "Saved";

    public OnFLy(Application application) {
        super(application);
        this.application = application;
        this.v = LayoutInflater.from(application).inflate(R.layout.popup_layout, this);
        iom = IOManager.getInstance(application);
        save = v.findViewById(R.id.save);
        body = v.findViewById(R.id.note);
        cancel = v.findViewById(R.id.cancel);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(SingleNoteViewModel.class);
        viewModel.getNoteLiveData().observeForever(note -> body.setText(note.body));
        save.setOnClickListener(v1 -> {
            save();
            doClose();
            TileService.lastNoteId = null;

        });
        cancel.setOnClickListener(v1 -> {
            toastText = "Canceled";
            doClose();
            TileService.lastNoteId = null;
        });
        showHideKeyboard();
        if (TileService.lastNoteId != null)
            viewModel.getNote(TileService.lastNoteId);
        else
            TileService.lastNoteId = 0L;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("?", "dispatchKeyEventPreIme: " + event);
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (body.getText().toString().isEmpty()) {
                toastText = "Canceled";
            } else {
                save();
                toastText = "Kept";
            }
            doClose();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public abstract void close();

    private void doClose() {
        Toast.makeText(application, toastText, Toast.LENGTH_SHORT).show();
        close();
    }

    private void save() {
        toastText = "Saved";
        String body = this.body.getText().toString();
        Note newNote = new Note(body);
        if (TileService.lastNoteId != null)
            newNote.id = TileService.lastNoteId;
        viewModel.getNoteLiveData().setValue(newNote);
        if (newNote.body.isEmpty()) {
            toastText = "Canceled";
        } else {
            Future<Long> lastId = viewModel.edit(State.INSERT);
            new Thread(() -> {
                while (!lastId.isDone()) ;
                try {
                    TileService.lastNoteId = lastId.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ).start();
        }
    }

    private void showHideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        body.requestFocus();
    }

}
