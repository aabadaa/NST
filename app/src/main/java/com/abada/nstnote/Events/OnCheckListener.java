package com.abada.nstnote.Events;

import androidx.lifecycle.MutableLiveData;

public class OnCheckListener {
    private final MutableLiveData<Integer> selectedCount;

    public OnCheckListener(MutableLiveData<Integer> selectedCount) {
        this.selectedCount = selectedCount;
    }

    public void onCheck(boolean isChecked) {
        int x = selectedCount.getValue() + (isChecked ? 1 : -1);
        selectedCount.setValue(x);
    }

    public int getSelectedCount() {
        return selectedCount.getValue();
    }
}
