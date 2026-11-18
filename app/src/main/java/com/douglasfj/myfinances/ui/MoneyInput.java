package com.douglasfj.myfinances.ui;

import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

/** Signed reais with at most two decimal places, including pasted/replaced text. */
public final class MoneyInput {
    private MoneyInput() {}
    public static void configure(EditText field) {
        field.setKeyListener(DigitsKeyListener.getInstance("0123456789-,."));
        field.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        field.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            String next = dest.subSequence(0, dstart).toString() + source.subSequence(start, end)
                    + dest.subSequence(dend, dest.length());
            if (next.matches("-?[0-9]*([.,][0-9]{0,2})?")) return null;
            // Normalize pasted Brazilian currency/grouping without accepting fractional cents.
            if (end - start > 1) {
                try {
                    long cents = com.douglasfj.myfinances.data.Money.parse(next);
                    if (dstart == 0 && dend == dest.length())
                        return java.math.BigDecimal.valueOf(cents,2).toPlainString().replace('.', ',');
                } catch (IllegalArgumentException ignored) { }
            }
            return dest.subSequence(dstart, dend);
        }});
    }
}
