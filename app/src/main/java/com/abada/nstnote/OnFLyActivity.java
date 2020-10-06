package com.abada.nstnote;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OnFLyActivity extends AppCompatActivity {
    public final String TAG=this.getClass().getName();
    Button save,cancel;
    EditText note;
    String toastText = "";
    NoteAdapter noteAdapter = MainActivity.noteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.tile_label);
        setContentView(R.layout.activity_popup);

        setFinishOnTouchOutside(false);
        save=findViewById(R.id.save);
        cancel=findViewById(R.id.cancel);
        note=findViewById(R.id.note);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                toastText = "Saved";
                IOManager iom = new IOManager(OnFLyActivity.this);
                Note newNote = new Note(note.getText().toString());
                if (newNote.getBody().isEmpty()) {
                    toastText = "Canceled";
                } else {
                    iom.writeNote(newNote);
                    try {
                        noteAdapter.addItem(-1, newNote);
                    } catch (Exception e) {
                    }
                }
                OnFLyActivity.this.onBackPressed();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastText="Canceled";
                OnFLyActivity.this.onBackPressed();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        TileService.clicked=false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this,toastText,Toast.LENGTH_SHORT).show();
    }
}