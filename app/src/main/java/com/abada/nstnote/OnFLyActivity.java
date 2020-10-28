package com.abada.nstnote;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OnFLyActivity extends AppCompatActivity {
    Button save, cancel;
    EditText body;
    Note newNote;
    String toastText = "saved";
    IOManager iom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        setFinishOnTouchOutside(false);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        body = findViewById(R.id.note);
        iom = IOManager.getInstance(getApplication());
        save.setOnClickListener(v -> finish());
        cancel.setOnClickListener(v -> {
            toastText = "canceled";
            finish();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        TileService.clicked = false;
    }

    private void close() {
        String body = this.body.getText().toString();
        newNote = new Note(body.substring(0, Math.min(body.length(), 30)), body);
        if (toastText.equals("canceled") || newNote.getBody().isEmpty()) {
            toastText = "canceled";
        } else {
            iom.insert(newNote);
        }
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }
}