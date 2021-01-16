package com.abada.nstnote.UI;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.abada.nstnote.IOManager;
import com.abada.nstnote.R;

public class AskDeleteActivity extends AppCompatActivity {
    Button save, cancel;
    EditText text;
    Button yes, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        setFinishOnTouchOutside(false);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        text = findViewById(R.id.note);
        yes = findViewById(R.id.save);
        no = findViewById(R.id.cancel);
        EditText text = findViewById(R.id.note);
        text.setEnabled(false);
        text.setText("Are you sure?");
        text.setTextColor(getResources().getColor(R.color.fontColor));
        yes.setText("Yes");
        no.setText("No");

        yes.setOnClickListener(v1 -> {
            IOManager iom = IOManager.getInstance(this.getApplication());
            iom.deleteSelected();
            finish();
        });
        no.setOnClickListener(v -> finish());

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
