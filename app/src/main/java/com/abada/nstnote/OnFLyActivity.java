package com.abada.nstnote;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OnFLyActivity extends AppCompatActivity {
    public final String TAG=this.getClass().getName();
    Button save,cancel;
    EditText note;
    String toastText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        setFinishOnTouchOutside(false);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        note = findViewById(R.id.note);
        note.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(note.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                toastText = "Saved";
                IOManager iom = IOManager.getInstance(getApplication());
                Note newNote = new Note(note.getText().toString());
                if (newNote.getBody().isEmpty()) {
                    toastText = "Canceled";
                } else {
                    iom.insert(newNote);
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