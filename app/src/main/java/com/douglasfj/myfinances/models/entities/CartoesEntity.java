package com.douglasfj.myfinances.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CARTOES")
public class CartoesEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_CARTAO")
    public int idCartao;

    @ColumnInfo(name = "NOME_CARTAO")
    public String nomeCartao;

}
