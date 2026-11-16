package com.douglasfj.myfinances.data;

import com.douglasfj.myfinances.data.FinanceModels.Summary;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Derived forecast only: a closing balance is never inserted as another income. */
public final class BalanceProjection {
    private BalanceProjection() {}

    public static void calculate(List<Summary> history) {
        List<Summary> chronological = new ArrayList<>(history);
        chronological.sort(Comparator.comparingInt((Summary s) -> s.month.ano).thenComparingInt(s -> s.month.mes));
        long balance = 0;
        for (Summary summary : chronological) {
            summary.openingBalanceCents = balance;
            balance = summary.closingBalanceCents();
        }
    }
}
