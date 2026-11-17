package com.douglasfj.myfinances.data;

import androidx.room.Embedded;
import androidx.room.Ignore;
import com.douglasfj.myfinances.models.entities.*;
import com.douglasfj.myfinances.constantes.IndicadorDespesaPaga;
import java.time.YearMonth;
import java.util.List;

public final class FinanceModels {
    private FinanceModels() {}

    public static class Entry {
        @Embedded public ValoresEntity value;
        public long linkId;
        public IndicadorDespesaPaga paid;
        public String cardName;
        @Ignore public Long invoiceCardId;
        @Ignore public YearMonth invoiceMonth;
    }

    public static class Summary {
        @Embedded public MesesEntity month;
        public double income;
        public double expenses;
        public double taxes;
        @Ignore public long openingBalanceCents;

        public long incomeCents() { return Money.cents(income); }
        public long expensesCents() { return Money.cents(expenses); }
        public long netCents() { return incomeCents() - expensesCents(); }
        public long availableCents() { return openingBalanceCents + incomeCents(); }
        public long closingBalanceCents() { return openingBalanceCents + netCents(); }
    }

    public static class Purchase {
        @Embedded public ComprasCartoesEntity purchase;
        public String cardName;
    }

    public static class State {
        public final YearMonth month;
        public final List<Entry> entries;
        public final List<Summary> history;
        public final List<CartoesEntity> cards;
        public final List<Purchase> purchases;
        public State(YearMonth month, List<Entry> entries, List<Summary> history,
                     List<CartoesEntity> cards, List<Purchase> purchases) {
            this.month = month; this.entries = entries; this.history = history;
            this.cards = cards; this.purchases = purchases;
            BalanceProjection.calculate(history);
        }

        public Summary summary() {
            for (Summary summary : history) {
                if (summary.month.ano == month.getYear() && summary.month.mes == month.getMonthValue()) return summary;
            }
            throw new IllegalStateException("Mês selecionado não encontrado no histórico.");
        }

        public CardInvoice invoice(long cardId) { return CardInvoice.calculate(this, cardId); }

        /** Presentation rows only. Invoice totals must never be inserted into VALORES. */
        public List<Entry> expenses() {
            List<Entry> rows = new java.util.ArrayList<>();
            for (Entry entry : entries) {
                if (entry.value.indicadorTipoValor == com.douglasfj.myfinances.constantes.IndicadorTipoValor.DESPESA
                        && entry.value.idCompraCartao == null) rows.add(entry);
            }
            for (CartoesEntity card : cards) {
                CardInvoice invoice = invoice(card.idCartao);
                if (invoice.lines.isEmpty()) continue;
                Entry row = new Entry(); row.value = new ValoresEntity();
                row.value.nomeValor = "Fatura " + card.nomeCartao;
                row.value.valorReais = invoice.totalCents / 100.0;
                row.value.indicadorTipoValor = com.douglasfj.myfinances.constantes.IndicadorTipoValor.DESPESA;
                row.value.tipoDespesa = com.douglasfj.myfinances.constantes.TipoDespesa.EXTRAS;
                row.invoiceCardId = card.idCartao; row.invoiceMonth = month;
                row.cardName = card.nomeCartao;
                row.paid = invoice.isPaid() ? IndicadorDespesaPaga.PAGA : IndicadorDespesaPaga.NAO_PAGA;
                for (CardInvoice.Line line : invoice.lines) {
                    java.util.Date date = line.entry.value.dataInsercaoValor;
                    if (date != null && (row.value.dataInsercaoValor == null || date.after(row.value.dataInsercaoValor))) row.value.dataInsercaoValor = date;
                }
                rows.add(row);
            }
            rows.sort((a,b) -> Long.compare(b.value.dataInsercaoValor == null ? 0 : b.value.dataInsercaoValor.getTime(),
                    a.value.dataInsercaoValor == null ? 0 : a.value.dataInsercaoValor.getTime()));
            return rows;
        }
    }
}
