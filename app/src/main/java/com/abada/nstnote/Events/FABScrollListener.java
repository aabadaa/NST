package com.abada.nstnote.Events;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FABScrollListener extends RecyclerView.OnScrollListener {
    private final FloatingActionButton fab;

    public FABScrollListener(FloatingActionButton fab) {
        this.fab = fab;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        Log.i("TAG", "onScrolled: " + dx + " " + dy);
        if (dy > 0 && fab.isShown())
            fab.hide();
        else
            fab.show();
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

}
