package com.abada.nstnote.Utilities;

import android.util.Log;

import androidx.room.TypeConverter;

public class CheckObjectConverter {
    @TypeConverter
    public String toString(Checkable.CheckObject obj) {
        return obj == null ? "no" : "yes";
    }

    @TypeConverter
    public Checkable.CheckObject toCheckObject(String s) {
        Log.i("TAG", "toCheckObject: " + s);
        if (s == null || s.equalsIgnoreCase("no"))
            return null;
        return new Checkable.CheckObject();
    }
}
