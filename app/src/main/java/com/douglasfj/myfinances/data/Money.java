package com.douglasfj.myfinances.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class Money {
    private Money() {}
    public static long parse(String text) {
        String cleaned = text.trim().replace("R$", "").replace("\u00a0", "").replace(" ", "");
        if (cleaned.contains(",")) cleaned = cleaned.replace(".", "").replace(',', '.');
        if (!cleaned.matches("-?[0-9]+(\\.[0-9]{1,2})?"))
            throw new IllegalArgumentException("Informe um valor válido com até duas casas decimais.");
        try {
            long cents = new BigDecimal(cleaned).movePointRight(2).longValueExact();
            if (cents == 0 || cents < -99999999999L || cents > 99999999999L) throw new ArithmeticException();
            return cents;
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Informe um valor diferente de zero, entre -R$ 999.999.999,99 e R$ 999.999.999,99.");
        }
    }
    public static long cents(BigDecimal value) { return value.movePointRight(2).longValueExact(); }
    public static String input(BigDecimal value) { return value.setScale(2).toPlainString().replace('.', ','); }
    public static long cents(double value) {
        return BigDecimal.valueOf(value).movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
    public static String format(long cents) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(BigDecimal.valueOf(cents, 2));
    }
    public static String input(double value) { return BigDecimal.valueOf(cents(value), 2).toPlainString().replace('.', ','); }
    public static long[] split(long total, int count) {
        if (total == 0 || count < 1 || count > 120 || total == Long.MIN_VALUE || Math.abs(total) < count)
            throw new IllegalArgumentException("Use de 1 a 120 parcelas, com pelo menos R$ 0,01 em cada parcela.");
        long[] values = new long[count];
        for (int i = 0; i < count; i++) values[i] = total / count + (i < Math.abs(total % count) ? Long.signum(total) : 0);
        return values;
    }
}
