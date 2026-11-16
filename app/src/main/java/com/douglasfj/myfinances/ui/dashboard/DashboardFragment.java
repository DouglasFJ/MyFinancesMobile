package com.douglasfj.myfinances.ui.dashboard;

import android.widget.LinearLayout;
import com.douglasfj.myfinances.ui.*;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import com.douglasfj.myfinances.constantes.*;
import java.time.YearMonth;

public class DashboardFragment extends FinancePageFragment {
    @Override protected void render(LinearLayout content, State state) {
        FinanceUi.text(content,"Seus meses",26); monthSelector(content,state);
        FinanceUi.text(content,"Receitas, despesas e impostos do mês selecionado.",14);
        Summary selected = state.summary();
        LinearLayout totals=FinanceUi.card(content);
        FinanceUi.text(totals,"Saldo acumulado previsto: " + Money.format(selected.closingBalanceCents()),22);
        FinanceUi.text(totals,"Saldo anterior: " + Money.format(selected.openingBalanceCents) +
                "\nReceitas do mês: " + Money.format(selected.incomeCents()) +
                "\nDisponível no mês: " + Money.format(selected.availableCents()) +
                "\nDespesas: " + Money.format(selected.expensesCents()) +
                "\nImpostos incluídos nas despesas: " + Money.format(Money.cents(selected.taxes)) +
                "\nResultado do mês: " + Money.format(selected.netCents()),15);
        FinanceUi.text(totals,"O saldo anterior é calculado a partir dos meses cadastrados e atualizado quando seus lançamentos mudam. A previsão inclui despesas pagas e pendentes.",13);
        FinanceUi.button(content,"Adicionar receita",v -> FinanceDialogs.entry(this,null,true));
        FinanceUi.button(content,"Adicionar despesa ou imposto",v -> FinanceDialogs.entry(this,null,false));
        FinanceUi.text(content,"Toque em um lançamento para editar, excluir ou registrar o pagamento.",13);
        for(int group=0;group<3;group++) {
            FinanceUi.text(content,new String[]{"Receitas","Despesas","Impostos"}[group],21); int count=0;
            for(Entry e:state.entries) {
                int kind=e.value.indicadorTipoValor==IndicadorTipoValor.RECEITA ? 0 : e.value.imposto ? 2 : 1;
                if(kind!=group) continue; count++;
                LinearLayout row=FinanceUi.card(content);
                FinanceUi.text(row,e.value.nomeValor + " · " + Money.format(Money.cents(e.value.valorReais)),18);
                String details=e.value.mesInicioFixo != null ? "Fixo mensal" : "Variável";
                if(e.value.idCompraCartao!=null) details="Cartão " + e.cardName;
                if(kind!=0) details+=" · " + (e.paid==IndicadorDespesaPaga.PAGA ? "Paga" : "Pendente");
                FinanceUi.text(row,details,14); row.setOnClickListener(v -> FinanceDialogs.entryActions(this,e));
            }
            if(count==0) FinanceUi.text(content,"Nenhum lançamento nesta categoria.",14);
        }
        FinanceUi.text(content,"Histórico mensal",23);
        for(Summary s:state.history) {
            LinearLayout row=FinanceUi.card(content); YearMonth month=YearMonth.of(s.month.ano,s.month.mes);
            FinanceUi.text(row,FinanceUi.month(month),18);
            FinanceUi.text(row,"Saldo anterior " + Money.format(s.openingBalanceCents) +
                    "\nReceitas " + Money.format(s.incomeCents()) + "\nDespesas " + Money.format(s.expensesCents()) +
                    "\nResultado do mês " + Money.format(s.netCents()) +
                    "\nSaldo acumulado " + Money.format(s.closingBalanceCents()),14);
            row.setOnClickListener(v -> model.selectMonth(month));
        }
    }
}
