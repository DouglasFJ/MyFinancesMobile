package com.douglasfj.myfinances.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.douglasfj.myfinances.constantes.IndicadorTipoValor;
import com.douglasfj.myfinances.constantes.IndicadorValorFixo;
import com.douglasfj.myfinances.constantes.TipoDespesa;

import java.util.Date;

@Entity(tableName = "VALORES")
public class ValoresEntity {
    public ValoresEntity() {
    }

    @Ignore
    public ValoresEntity(String nomeValor, double valorReais, Date dataInsercaoValor, IndicadorValorFixo indicadorValorFixo, IndicadorTipoValor indicadorTipoValor, TipoDespesa tipoValor, Long idCompraCartao) {
        this.nomeValor = nomeValor;
        this.valorReais = valorReais;
        this.dataInsercaoValor = dataInsercaoValor;
        this.indicadorValorFixo = indicadorValorFixo;
        this.indicadorTipoValor = indicadorTipoValor;
        this.tipoDespesa = tipoValor;
        this.idCompraCartao = idCompraCartao;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_VALOR")
    public Long idValor;

    @ColumnInfo(name = "NOME_VALOR")
    public String nomeValor;

    @ColumnInfo(name = "VALOR_REAIS")
    public double valorReais;

    @ColumnInfo(name = "DATA_INSERCAO_VALOR")
    public Date dataInsercaoValor;

    @ColumnInfo(name = "INDICADOR_VALOR_FIXO")
    public IndicadorValorFixo indicadorValorFixo;

    @ColumnInfo(name = "INDICADOR_TIPO_VALOR")
    public IndicadorTipoValor indicadorTipoValor;

    @ColumnInfo(name = "TIPO_DESPESA")
    public TipoDespesa tipoDespesa;

    @ColumnInfo(name = "ID_COMPRA_CARTAO")
    public Long idCompraCartao;

}
