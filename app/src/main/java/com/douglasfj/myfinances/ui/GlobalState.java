package com.douglasfj.myfinances.ui;

import androidx.lifecycle.MutableLiveData;

import com.douglasfj.myfinances.models.entities.MesesEntity;

import java.util.Calendar;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class GlobalState {

    public static final MutableLiveData<Boolean> IS_VALUES_VISIBLE = new MutableLiveData<>(true);
    public static final BehaviorSubject<MesesEntity> MES_SELECIONADO = BehaviorSubject.createDefault(getMesAtual());

    private static MesesEntity getMesAtual(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)+1;
        if(cal.get(Calendar.DAY_OF_MONTH) >= 21) month++;

        return new MesesEntity(month, cal.get(Calendar.YEAR));
    }
}
