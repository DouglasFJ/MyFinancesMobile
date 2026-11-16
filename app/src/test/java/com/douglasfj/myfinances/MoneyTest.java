package com.douglasfj.myfinances;

import com.douglasfj.myfinances.data.Money;
import org.junit.Test;
import static org.junit.Assert.*;

public class MoneyTest {
    @Test public void acceptsBrazilianCurrencyAndDecimalPoint() {
        assertEquals(123456,Money.parse("R$ 1.234,56"));
        assertEquals(123456,Money.parse("1234.56"));
        assertEquals(1,Money.parse("0,01"));
    }
    @Test public void rejectsInvalidAmounts() {
        for(String invalid:new String[]{"0","-1","1,234","abc","NaN","1.000.00","9999999999999999999999"}) {
            assertThrows(IllegalArgumentException.class,() -> Money.parse(invalid));
        }
    }
    @Test public void installmentRemainderPreservesEveryCent() {
        assertArrayEquals(new long[]{3334,3333,3333},Money.split(10000,3));
        assertArrayEquals(new long[]{1,1,1},Money.split(3,3));
        for(int count=1;count<=120;count++) {
            long sum=0; for(long part:Money.split(125299,count)) sum+=part;
            assertEquals(125299,sum);
        }
    }
    @Test public void rejectsZeroAndFractionalCentInstallments() {
        assertThrows(IllegalArgumentException.class,() -> Money.split(100,0));
        assertThrows(IllegalArgumentException.class,() -> Money.split(100,121));
        assertThrows(IllegalArgumentException.class,() -> Money.split(2,3));
    }
}
