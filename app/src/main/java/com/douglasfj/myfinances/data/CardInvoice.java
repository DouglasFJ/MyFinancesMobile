package com.douglasfj.myfinances.data;

import com.douglasfj.myfinances.constantes.IndicadorDespesaPaga;
import com.douglasfj.myfinances.data.FinanceModels.*;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** A card's monthly bill is a view of its installments, never an extra expense. */
public final class CardInvoice {
    public static final class Line {
        public final Entry entry;
        public final String name;
        public final long installmentCents;
        public final long purchaseCents;
        public final int installmentNumber;
        public final int installmentCount;

        private Line(Entry entry, Purchase purchase, YearMonth month) {
            this.entry = entry;
            name = purchase.purchase.nomeCompra;
            installmentCents = Money.cents(entry.value.valorReais);
            purchaseCents = Money.cents(purchase.purchase.valorCompra);
            installmentCount = purchase.purchase.numeroParcelas;
            if (purchase.purchase.dataPrimeiraParcela == null) installmentNumber = 0;
            else {
                YearMonth start = YearMonth.from(purchase.purchase.dataPrimeiraParcela.toInstant().atZone(ZoneId.systemDefault()));
                installmentNumber = Math.toIntExact(ChronoUnit.MONTHS.between(start, month) + 1);
            }
        }
    }

    public final List<Line> lines;
    public final long totalCents;
    public final long pendingCents;

    private CardInvoice(List<Line> lines, long total, long pending) {
        this.lines = Collections.unmodifiableList(lines);
        totalCents = total;
        pendingCents = pending;
    }

    public static CardInvoice calculate(State state, long cardId) {
        Map<Long, Purchase> purchases = new HashMap<>();
        for (Purchase purchase : state.purchases) {
            if (purchase.purchase.idCartao == cardId) purchases.put(purchase.purchase.idCompraCartao, purchase);
        }
        List<Line> lines = new ArrayList<>();
        long total = 0, pending = 0;
        for (Entry entry : state.entries) {
            Purchase purchase = purchases.get(entry.value.idCompraCartao);
            if (purchase == null) continue;
            Line line = new Line(entry, purchase, state.month);
            lines.add(line);
            total += line.installmentCents;
            if (entry.paid != IndicadorDespesaPaga.PAGA) pending += line.installmentCents;
        }
        return new CardInvoice(lines, total, pending);
    }
}
