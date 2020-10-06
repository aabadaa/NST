package com.abada.nstnote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity {
    final String TAG = this.getClass().getName();
    EditText header, body;
    Note note = new Note();
    int notePosition = -1;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.i(TAG, "onCreate: ");
        Intent intent = getIntent();
        header = findViewById(R.id.note_header);
        body = findViewById(R.id.note_text);
        noteAdapter = MainActivity.noteAdapter;

        if (intent.hasExtra(Note.POSITION)) {
            notePosition = intent.getIntExtra(Note.POSITION, -1);
            note = noteAdapter.getItem(notePosition);
            header.setText(note.getHeader());
            body.setText(note.getBody());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    void save() {
        Log.i(TAG, "save: ");
        IOManager iom = new IOManager(this);

        Note newNote = new Note(header.getText().toString(), body.getText().toString());

        if (!note.equalsIgnoreDate(newNote)) {
            if (!newNote.isEmpty()) {
                iom.writeNote(newNote);
                noteAdapter.addItem(notePosition, newNote);
            }
            iom.deleteNote(note);
            noteAdapter.removeItem(notePosition);
        } else if (note.isEmpty()) {
            iom.deleteNote(note);
            iom.deleteNote(newNote);
            noteAdapter.removeItem(notePosition);
        }

        Log.i(TAG, "save: finish");
    }
}