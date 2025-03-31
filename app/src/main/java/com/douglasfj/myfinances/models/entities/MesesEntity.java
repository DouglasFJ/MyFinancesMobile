package com.douglasfj.myfinances.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Entity(tableName = "MESES")
public class MesesEntity {

    public MesesEntity() {}

    @Ignore
    public MesesEntity(int mes, int ano) {
        this.mes = mes;
        this.ano = ano;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_MES")
    public Long idMes;

    @ColumnInfo(name = "MES")
    public int mes;

    @ColumnInfo(name = "ANO")
    public int ano;

    @Override
    public String toString() {
        String month = Month.of(mes).getDisplayName(TextStyle.SHORT, Locale.getDefault());
        return month+"/"+String.valueOf(ano).substring(2);
    }
}
