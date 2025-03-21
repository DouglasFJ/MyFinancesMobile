package com.douglasfj.myfinances.models;

import com.douglasfj.myfinances.constantes.TipoDespesa;

public class ItemDepesa {

    private String nomeDespesa;
    private double valorDespesa;

    private TipoDespesa tipoDespesa;

    public ItemDepesa(String nomeDespesa, double valorDespesa, TipoDespesa tipoDespesa) {
        this.nomeDespesa = nomeDespesa;
        this.valorDespesa = valorDespesa;
        this.tipoDespesa = tipoDespesa;
    }

    public String getNomeDespesa() {
        return nomeDespesa;
    }

    public void setNomeDespesa(String nomeDespesa) {
        this.nomeDespesa = nomeDespesa;
    }

    public double getValorDespesa() {
        return valorDespesa;
    }

    public void setValorDespesa(float valorDespesa) {
        this.valorDespesa = valorDespesa;
    }

    public TipoDespesa getTipoDespesa() {
        return tipoDespesa;
    }

    public void setTipoDespesa(TipoDespesa tipoDespesa) {
        this.tipoDespesa = tipoDespesa;
    }

}
