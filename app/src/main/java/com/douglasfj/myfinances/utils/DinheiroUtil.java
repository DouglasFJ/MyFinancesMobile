package com.douglasfj.myfinances.utils;

import androidx.annotation.NonNull;

public class DinheiroUtil {

    public static String floatToStringReais(java.math.BigDecimal valor) {
        return com.douglasfj.myfinances.data.Money.format(com.douglasfj.myfinances.data.Money.cents(valor));
    }

    @NonNull
    public static String floatToStringReais(double valor) {
        return "R$ " + String.format("%.2f", valor);
    }
}
