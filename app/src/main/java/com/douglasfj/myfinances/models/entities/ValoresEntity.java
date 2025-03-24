package com.douglasfj.myfinances.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.douglasfj.myfinances.constantes.TipoDespesa;

import java.util.Date;

@Entity(tableName = "VALORES")
public class ValoresEntity {
    public ValoresEntity() {
    }

    public ValoresEntity(String nomeValor, double valorReais, Date dataInsercaoValor, int indicadorValorFixo, int indicadorValorDespesa, int indicadorDespesaPaga, String tipoValor, Long idCompraCartao) {
        this.nomeValor = nomeValor;
        this.valorReais = valorReais;
        this.dataInsercaoValor = dataInsercaoValor;
        this.indicadorValorFixo = indicadorValorFixo;
        this.indicadorValorDespesa = indicadorValorDespesa;
        this.indicadorDespesaPaga = indicadorDespesaPaga;
        this.tipoValor = tipoValor;
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
    public int indicadorValorFixo;

    @ColumnInfo(name = "INDICADOR_VALOR_DESPESA")
    public int indicadorValorDespesa;

    @ColumnInfo(name = "INDICADOR_DESPESA_PAGA")
    public int indicadorDespesaPaga;

    @ColumnInfo(name = "TIPO_VALOR")
    public String tipoValor;

    @ColumnInfo(name = "ID_COMPRA_CARTAO")
    public Long idCompraCartao;

    @Ignore
    public TipoDespesa getTipoDespesa() {
        return TipoDespesa.valueOf(tipoValor);
    }
}
