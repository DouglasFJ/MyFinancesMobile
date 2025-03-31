package com.douglasfj.myfinances.converters;

import androidx.room.TypeConverter;

import com.douglasfj.myfinances.constantes.IndicadorDespesaPaga;
import com.douglasfj.myfinances.constantes.IndicadorTipoValor;
import com.douglasfj.myfinances.constantes.IndicadorValorFixo;
import com.douglasfj.myfinances.constantes.TipoDespesa;

import java.util.Date;

public class MyFinancesConverters {
    // DATE
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    // INDICADOR DESPESA PAGA
    @TypeConverter
    public static IndicadorDespesaPaga indicadorDespesaPagaFromInteger(Integer value) {
        if (value == null) return null;
        else {
            return value == 0? IndicadorDespesaPaga.NAO_PAGA:IndicadorDespesaPaga.PAGA;
        }
    }

    @TypeConverter
    public static Integer indicadorDespesaPagaToInteger(IndicadorDespesaPaga value) {
        if (value == null) return null;
        else {
            return value.equals(IndicadorDespesaPaga.NAO_PAGA)?0:1;
        }
    }

    // INDICADOR VALOR FIXO
    @TypeConverter
    public static IndicadorValorFixo indicadorValorFixoFromInteger(Integer value) {
        if (value == null) return null;
        else {
            return value == 0? IndicadorValorFixo.NAO_FIXO:IndicadorValorFixo.FIXO;
        }
    }

    @TypeConverter
    public static Integer indicadorValorFixoToInteger(IndicadorValorFixo value) {
        if (value == null) return null;
        else {
            return value.equals(IndicadorValorFixo.NAO_FIXO)?0:1;
        }
    }

    // INDICADOR TIPO VALOR
    @TypeConverter
    public static IndicadorTipoValor indicadorTipoValorFromInteger(Integer value) {
        if (value == null) return null;
        else {
            return value == 0? IndicadorTipoValor.DESPESA:IndicadorTipoValor.RECEITA;
        }
    }

    @TypeConverter
    public static Integer indicadorTipoValorToInteger(IndicadorTipoValor value) {
        if (value == null) return null;
        else {
            return value.equals(IndicadorTipoValor.DESPESA)?0:1;
        }
    }

    // TIPO DESPESA
    @TypeConverter
    public static TipoDespesa tipoDespesaFromString(String value) {
        if (value == null) return null;
        else return TipoDespesa.valueOf(value);
    }

    @TypeConverter
    public static String tipoDespesaToString(IndicadorTipoValor value) {
        if (value == null) return null;
        else return value.name();
    }

}
