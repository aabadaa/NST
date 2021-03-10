package com.abada.nstnote.UI;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.abada.nstnote.Note;
import com.abada.nstnote.R;
import com.abada.nstnote.ViewModels.SingleNoteModel;

public class NoteActivity extends AppCompatActivity {
    final String TAG = this.getClass().getName();
    long id = -2;
    EditText header, body;
    Note curNote;
    SingleNoteModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.i(TAG, "onCreate: ");

        header = findViewById(R.id.note_header);
        body = findViewById(R.id.note_text);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(SingleNoteModel.class);
        viewModel.getNote().observe(this, note -> {
            header.setText(note.getHeader());
            body.setText(note.getBody());
        });

        if (getIntent().hasExtra(Note.ID)) {
            viewModel.getNote(id = getIntent().getLongExtra(Note.ID, -2));
            curNote = viewModel.getNote().getValue();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.getNote().setValue(new Note(header.getText().toString(), body.getText().toString()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();
    }

    void save() {
        String header = this.header.getText().toString();
        String body = this.body.getText().toString();
        Note note = new Note(header, body);
        if (id != -2)
            note.id = id;
        viewModel.getNote().setValue(note);
        if (id != -2) {
            if (!note.equalsIgnoreDate(curNote))
                if (note.isEmpty())
                    viewModel.edit(SingleNoteModel.DELETE);
                else
                    viewModel.edit(SingleNoteModel.UPDATE);

        } else if (!note.isEmpty())
            viewModel.edit(SingleNoteModel.INSERT);
    }
}