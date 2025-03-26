package com.douglasfj.myfinances.ui;

import androidx.lifecycle.MutableLiveData;

import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.models.entities.MesesEntity;

import java.util.Calendar;
import java.util.Date;

public class GlobalState {

    public static final MutableLiveData<Boolean> IS_VALUES_VISIBLE = new MutableLiveData<>(true);
    public static final MutableLiveData<MesesEntity> MES_SELECIONADO = new MutableLiveData<>(getMesAtual());

    private static MesesEntity getMesAtual(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)+1;
        if(cal.get(Calendar.DAY_OF_MONTH) >= 21) month++;

        return new MesesEntity(month, cal.get(Calendar.YEAR));
    }
}
