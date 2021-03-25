package com.abada.nstnote.Utilities;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

public class Checkable {
    public final static MutableLiveData<Integer> counter = new MutableLiveData<>();
    private static int realCounter;
    @TypeConverters(CheckObjectConverter.class)
    @ColumnInfo(name = "checker")
    public CheckObject checker = null;

    public static int getCheckCounter() {
        return realCounter;
    }

    public final void check() {
        if (checker == null)
            checker = new CheckObject();
        else {
            checker.uncheck();
            checker = null;
        }
    }

    public final boolean isChecked() {
        return checker != null;
    }

    static public class CheckObject {
        public CheckObject() {
            realCounter++;
            Log.i("TAG", "CheckObject: " + counter.getValue());
            counter.postValue(realCounter);
        }

        public void uncheck() {
            counter.postValue(--realCounter);

        }

    }
}