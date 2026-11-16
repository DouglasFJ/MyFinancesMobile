package com.douglasfj.myfinances;

import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.Summary;
import com.douglasfj.myfinances.models.entities.MesesEntity;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class BalanceProjectionTest {
    private Summary month(int year, int month, double income, double expenses) {
        Summary summary = new Summary();
        summary.month = new MesesEntity(month, year);
        summary.income = income; summary.expenses = expenses;
        return summary;
    }

    @Test public void reproducesSpreadsheetWithoutCountingCarryoverAsIncome() {
        Summary november = month(2026, 11, 1394.16, 0);
        Summary december = month(2026, 12, 11700.68, 10589.74);
        List<Summary> history = Arrays.asList(december, november);
        BalanceProjection.calculate(history);
        assertEquals(139416, december.openingBalanceCents);
        assertEquals(1170068, december.incomeCents());
        assertEquals(1309484, december.availableCents());
        assertEquals(111094, december.netCents());
        assertEquals(250510, december.closingBalanceCents());
        assertSame(december, history.get(0)); // Preserve the dashboard's descending order.
    }

    @Test public void carriesDeficitsAcrossYearsAndRecalculatesHistoricalChanges() {
        Summary december = month(2026, 12, 100, 150);
        Summary january = month(2027, 1, 100, 25);
        Summary february = month(2027, 2, 0, 0);
        List<Summary> history = Arrays.asList(february, january, december);
        BalanceProjection.calculate(history);
        assertEquals(-5000, january.openingBalanceCents);
        assertEquals(2500, january.closingBalanceCents());
        assertEquals(2500, february.closingBalanceCents());
        december.expenses = 200;
        BalanceProjection.calculate(history);
        assertEquals(-10000, january.openingBalanceCents);
        assertEquals(-2500, february.closingBalanceCents());
        BalanceProjection.calculate(history);
        assertEquals(-2500, february.closingBalanceCents());
        assertEquals(0, december.openingBalanceCents);
    }
}
