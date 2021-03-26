package com.abada.nstnote.UI.Activities;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.abada.nstnote.UI.OnFLy;

public class OnFLyActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShowWhenLocked(true);
        OnFLy o = new OnFLy(getApplication()) {
            @Override
            public void close() {
                finish();
            }
        };
        addContentView(o, new ConstraintLayout.LayoutParams(-2, -2));
    }

}