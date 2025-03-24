package com.douglasfj.myfinances.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isValuesVisible;

    public HomeViewModel() {
        isValuesVisible = new MutableLiveData<>();
        isValuesVisible.setValue(true);
    }

    public MutableLiveData<Boolean> getIsValuesVisible() { return isValuesVisible; }

}