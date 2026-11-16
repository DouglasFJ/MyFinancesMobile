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
    }
}
