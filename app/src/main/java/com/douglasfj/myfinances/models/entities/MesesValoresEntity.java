package com.douglasfj.myfinances.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MESES_VALORES")
public class MesesValoresEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_MES_VALOR")
    public Long idMesValor;

    @ColumnInfo(name = "ID_MES")
    public Long idMes;

    @ColumnInfo(name = "ID_VALOR")
    public Long idValor;
}
