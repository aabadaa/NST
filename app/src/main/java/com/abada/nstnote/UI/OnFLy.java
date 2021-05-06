package com.abada.nstnote.UI;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.abada.nstnote.Note;
import com.abada.nstnote.R;
import com.abada.nstnote.Utilities.State;
import com.abada.nstnote.ViewModels.SingleNoteViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import static com.abada.nstnote.TileService.lastNoteId;

public abstract class OnFLy extends FrameLayout {
    private final EditText body;
    //others
    private final Application application;
    //Models
    SingleNoteViewModel viewModel;
    private String toastText;

    public OnFLy(LayoutInflater inflater, Application application) {
        super(application);
        this.application = application;
        application.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        View v = inflater.inflate(R.layout.popup_layout, this);
        Button save = v.findViewById(R.id.save);
        save.setOnClickListener(v1 -> save(true));
        body = v.findViewById(R.id.note);
        Button cancel = v.findViewById(R.id.cancel);
        cancel.setOnClickListener(v1 -> cancel());
        showHideKeyboard();
        viewModel = new AndroidViewModelFactory(application).create(SingleNoteViewModel.class);
        viewModel.getNoteLiveData().observeForever(note -> {
            body.setText(note.body);
            body.setSelection(body.length());
        });
        try {
            viewModel.getNote(lastNoteId != null ? lastNoteId.get().get(0) : 0);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("?", "dispatchKeyEventPreIme: " + event);
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (body.getText().toString().isEmpty())
                cancel();
            else
                save(false);

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public abstract void close();

    private void doClose() {
        Toast.makeText(application, toastText, Toast.LENGTH_SHORT).show();
        close();
    }

    private void save(boolean doPop) {
        assert viewModel.getNoteLiveData().getValue() != null;
        toastText = "Saved";
        String body = this.body.getText().toString();
        Note newNote = new Note(body);
        newNote.id = viewModel.getNoteLiveData().getValue().id;

        viewModel.getNoteLiveData().setValue(newNote);
        if (newNote.body.isEmpty()) {
            cancel();
        } else {
            Future<List<Long>> id = viewModel.edit(State.INSERT);
            if (doPop) {
                lastNoteId = null;
            } else {
                toastText = "Kept";
                lastNoteId = id;
            }
        }
        doClose();
    }

    private void showHideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        body.requestFocus();
    }

    private void cancel() {
        toastText = "Canceled";
        viewModel.edit(State.DELETE);
        lastNoteId = null;
        doClose();
    }
}
