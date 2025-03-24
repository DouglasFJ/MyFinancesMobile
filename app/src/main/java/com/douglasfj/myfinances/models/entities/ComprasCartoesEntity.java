package com.douglasfj.myfinances.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "COMPRAS_CARTOES")
public class ComprasCartoesEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_COMPRA_CARTAO")
    public Long idCompraCartao;

    @ColumnInfo(name = "ID_CARTAO")
    public int idCartao;

    @ColumnInfo(name = "NOME_COMPRA")
    public String nomeCompra;

    @ColumnInfo(name = "VALOR_COMPRA")
    public double valorCompra;

    @ColumnInfo(name = "NUMERO_PARCELAS")
    public int numeroParcelas;

    @ColumnInfo(name = "DATA_PRIMEIRA_PARCELA")
    public Date dataPrimeiraParcela;

}
