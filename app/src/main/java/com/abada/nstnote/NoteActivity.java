package com.abada.nstnote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class NoteActivity extends AppCompatActivity {
    final String TAG = this.getClass().getName();
    EditText header, body;
    MutableLiveData<Note> note;
    Note curNote;
    IOManager iom;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.i(TAG, "onCreate: ");
        intent = getIntent();
        header = findViewById(R.id.note_header);
        body = findViewById(R.id.note_text);
        iom = IOManager.getInstance(getApplication());
        note = iom.getNote();
        note.observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                header.setText(note.getHeader());
                body.setText(note.getBody());
                curNote = note;
            }
        });
        if (intent.hasExtra(Note.ID)) {
            long id = intent.getLongExtra(Note.ID, -1);
            Log.i(TAG, "onCreate: " + id);
            iom.getNoteById(id);
        } else
            note.setValue(new Note());
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
        String header = this.header.getText().toString();
        String body = this.body.getText().toString();
        Note note = new Note(header, body);
        if (curNote != null)
            note.id = curNote.id;
        if (intent.hasExtra(Note.ID)) {
            if (!note.equalsIgnoreDate(curNote))
                if (note.isEmpty())
                    iom.deleteNote(note);
                else
                    iom.update(note);
        } else if (!note.isEmpty())
            iom.insert(note);
    }
}