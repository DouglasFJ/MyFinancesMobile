package com.douglasfj.myfinances.utils;

import androidx.annotation.NonNull;

public class DinheiroUtil {

    @NonNull
    public static String floatToStringReais(double valor) {
        return "R$ " + String.format("%.2f", valor);
    }
}
