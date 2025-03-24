package com.douglasfj.myfinances.constantes;

import androidx.annotation.Nullable;

import com.douglasfj.myfinances.R;

public enum TipoDespesa {
    INVESTIMENTO,
    ALIMENTACAO,
    ESSENCIAL,
    EXTRAS,
    MEDICACAO;

    public Integer getRIDIcon() {
        switch (this) {
            case EXTRAS:
                return R.drawable.coin;
            case ESSENCIAL:
                return R.drawable.base;
            case MEDICACAO:
                return R.drawable.pill;
            case ALIMENTACAO:
                return R.drawable.feed;
            case INVESTIMENTO:
                return R.drawable.investment;
            default:
                return R.drawable.coin;
        }
    }

    public String getColorName() {
        switch (this) {
            case EXTRAS:
                return "red";
            case ESSENCIAL:
            case INVESTIMENTO:
                return "emerald";
            case MEDICACAO:
            case ALIMENTACAO:
                return "amber";
            default:
                return "neutral";
        }
    }

}
