package com.douglasfj.myfinances.models.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.douglasfj.myfinances.constantes.IndicadorDespesaPaga;

@Entity(tableName = "MESES_VALORES")
public class MesesValoresEntity {

    public MesesValoresEntity(){}
    @Ignore
    public MesesValoresEntity(Long idMes, Long idValor, @Nullable IndicadorDespesaPaga indicadorDespesaPaga) {
        this.idMes = idMes;
        this.idValor = idValor;
        this.indicadorDespesaPaga = indicadorDespesaPaga;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_MES_VALOR")
    public Long idMesValor;

    @ColumnInfo(name = "ID_MES")
    public Long idMes;

    @ColumnInfo(name = "ID_VALOR")
    public Long idValor;

    @Nullable
    @ColumnInfo(name = "INDICADOR_DESPESA_PAGA")
    public IndicadorDespesaPaga indicadorDespesaPaga;
}
