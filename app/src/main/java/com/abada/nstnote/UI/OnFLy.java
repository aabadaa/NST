package com.abada.nstnote.UI;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.Utilities.State;
import com.abada.nstnote.ViewModels.NotesViewModel;
import com.abada.nstnote.databinding.PopupLayoutBinding;

import java.util.List;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import static com.abada.nstnote.Utilities.Tools.getNoteId;
import static com.abada.nstnote.Utilities.Tools.isKept;
import static com.abada.nstnote.Utilities.Tools.keepNote;

public abstract class OnFLy extends FrameLayout {
    PopupLayoutBinding binding;
    //others
    private final Application application;
    //Models
    NotesViewModel viewModel;
    Note cur;
    private String toastText;

    public OnFLy(LayoutInflater inflater, Application application) {
        super(application);
        this.application = application;
        application.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        binding = PopupLayoutBinding.inflate(inflater, this, true);
        binding.save.setOnClickListener(v1 -> save(true));
        binding.cancel.setOnClickListener(v1 -> cancel());
        showHideKeyboard();
        viewModel = new AndroidViewModelFactory(application).create(NotesViewModel.class);
        viewModel.getNoteById(isKept() ? getNoteId() : 0)
                .observeForever(note -> {
                    if (note != null) {
                        binding.body.setText(note.body);
                        cur = note;
                    }
                });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("?", "dispatchKeyEventPreIme: " + event);
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (binding.body.getText().toString().isEmpty())
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
        toastText = "Saved";
        String body = binding.body.getText().toString();
        Note newNote = new Note(body);
        if (cur != null) {
            newNote.id = cur.id;
            newNote.date = cur.date;
        }
        if (newNote.body.isEmpty()) {
            cancel();
        } else {
            LiveData<List<Long>> id = viewModel.edit(newNote, State.INSERT);
            if (!doPop) {
                toastText = "Kept";
                id.observeForever(list -> keepNote(list.get(0)));
            }
        }
        doClose();
    }

    private void showHideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        binding.body.requestFocus();
    }

    private void cancel() {
        toastText = "Canceled";
        if (cur != null)
            viewModel.edit(cur, State.DELETE);
        doClose();
    }
}
