package com.douglasfj.myfinances.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MESES")
public class MesesEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_MES")
    public Long idMes;

    @ColumnInfo(name = "MES")
    public int mes;

    @ColumnInfo(name = "ANO")
    public int ano;
}
