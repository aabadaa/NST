package com.abada.nstnote.UI;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.abada.nstnote.TileService;

public class OnFLyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnFLy o = new OnFLy(getApplication()) {
            @Override
            public void close() {
                TileService.clicked = false;
                finish();
            }
        };
        addContentView(o, new ConstraintLayout.LayoutParams(-2, -2));
    }


}