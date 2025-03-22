package com.douglasfj.myfinances.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "VALORES")
public class ValoresEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_VALOR")
    public int idValor;

    @ColumnInfo(name = "NOME_VALOR")
    public String nomeValor;

    @ColumnInfo(name = "VALOR_REAIS")
    public double valorReais;

    @ColumnInfo(name = "DATA_INSERCAO_VALOR")
    public Date dataInsercaoValor;

    @ColumnInfo(name = "INDICADOR_VALOR_FIXO")
    public int indicadorValorFixo;

    @ColumnInfo(name = "INDICADOR_VALOR_DESPESA")
    public int indicadorValorDespesa;

    @ColumnInfo(name = "TIPO_VALOR")
    public String tipoValor;

    @ColumnInfo(name = "ID_COMPRA_CARTAO")
    public int idCompraCartao;
}
