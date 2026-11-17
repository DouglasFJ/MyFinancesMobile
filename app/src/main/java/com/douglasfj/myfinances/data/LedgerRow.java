package com.douglasfj.myfinances.data;

import com.douglasfj.myfinances.constantes.*;
import com.douglasfj.myfinances.models.entities.ValoresEntity;
import java.time.YearMonth;

/** A monthly occurrence of a normal entry or an aggregated card invoice. */
public class LedgerRow {
    public long rowId;
    public Long valueId;
    public Long cardId;
    public int month;
    public int year;
    public String name;
    public double amount;
    public int paid;
    public int fixed;
    public boolean tax;

    public YearMonth period() { return YearMonth.of(year, month); }
    public boolean isInvoice() { return cardId != null; }
    public FinanceModels.Entry asEntry(boolean income) {
        if (isInvoice()) throw new IllegalStateException("Uma fatura não é um lançamento avulso.");
        FinanceModels.Entry entry = new FinanceModels.Entry();
        ValoresEntity value = new ValoresEntity();
        value.idValor = valueId; value.nomeValor = name; value.valorReais = amount;
        value.imposto = tax; value.indicadorTipoValor = income ? IndicadorTipoValor.RECEITA : IndicadorTipoValor.DESPESA;
        value.mesInicioFixo = fixed == 1 ? RoomFinanceRepository.key(period()) : null;
        entry.value = value; entry.linkId = rowId;
        entry.paid = paid == 1 ? IndicadorDespesaPaga.PAGA : IndicadorDespesaPaga.NAO_PAGA;
        return entry;
    }
}
