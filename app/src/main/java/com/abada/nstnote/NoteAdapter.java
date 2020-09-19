package com.abada.nstnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class NoteAdapter extends ArrayAdapter<Note> {

    public NoteAdapter(@NonNull Activity context, ArrayList<Note> list) {
        super(context, R.layout.list_item, list);
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        Note note = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        convertView.setTag(position);
        TextView header = convertView.findViewById(R.id.item_header);
        header.setText(note.getHeader());
        
        TextView date = convertView.findViewById(R.id.item_date);
        date.setText(note.getDate());
        date.setTag(position);
        return convertView;
    }
}
