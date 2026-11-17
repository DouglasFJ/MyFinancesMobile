package com.douglasfj.myfinances.ui.dashboard;

import android.widget.LinearLayout;
import android.widget.GridLayout;
import android.view.View;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.adapters.DespesaViewAdapter;
import com.douglasfj.myfinances.models.entities.ValoresEntity;
import java.util.ArrayList;
import java.util.List;
import com.douglasfj.myfinances.ui.*;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import com.douglasfj.myfinances.constantes.*;
import java.time.YearMonth;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.douglasfj.myfinances.ui.ledger.LedgerActivity;

public class DashboardFragment extends FinancePageFragment {
    private final ActivityResultLauncher<android.content.Intent> ledger=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                if(result.getResultCode()==android.app.Activity.RESULT_OK && model!=null) model.refresh();
            });
    @Override protected void render(LinearLayout content, State state) {
        FinanceUi.text(content,"Meses",26); monthSelector(content,state);
        Summary selected = state.summary();
        LinearLayout totals=FinanceUi.card(content);
        FinanceUi.text(totals,"Saldo acumulado: " + Money.format(selected.closingBalanceCents()),22);
        FinanceUi.text(totals,"Saldo mês anterior: " + Money.format(selected.openingBalanceCents) +
                "\nReceitas do mês: " + Money.format(selected.incomeCents()) +
                "\nTotal receitas: " + Money.format(selected.availableCents()) +
                "\n\nDespesas: " + Money.format(selected.expensesCents()) +
                "\nImpostos: " + Money.format(Money.cents(selected.taxes)),15);


        FinanceUi.button(content,"Adicionar receita",v -> FinanceDialogs.entry(this,null,true));
        FinanceUi.button(content,"Visualizar receitas",v -> ledger.launch(LedgerActivity.intent(requireContext(),true)));

        FinanceUi.button(content,"Adicionar despesa",v -> FinanceDialogs.entry(this,null,false))
                .setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(com.douglasfj.myfinances.R.color.red_700)));
        FinanceUi.button(content,"Visualizar despesas",v -> ledger.launch(LedgerActivity.intent(requireContext(),false)))
                .setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(com.douglasfj.myfinances.R.color.red_700)));
        FinanceUi.text(content,"Toque em um lançamento para editar, excluir ou registrar o pagamento.",13);

        for(int group=0;group<2;group++) {
            boolean income=group==0;
            LinearLayout layValor=FinanceUi.card(content);
            FinanceUi.textWithColor(layValor,income ? "Receitas" : "Despesas",21, income ? R.color.emerald_800: R.color.red_800);
            List<Entry> entries=new ArrayList<>();
            for(Entry entry:income ? state.entries : state.expenses()) {
                if(!income || entry.value.indicadorTipoValor==IndicadorTipoValor.RECEITA) entries.add(entry);
            }
            List<ValoresEntity> values=new ArrayList<>();
            for(Entry entry:entries) values.add(entry.value);
            DespesaViewAdapter adapter=new DespesaViewAdapter(requireContext(),values);
            GridLayout grid=new GridLayout(requireContext());
            grid.setColumnCount(2);
            grid.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
            layValor.addView(grid);
            for(int position=0;position<Math.min(5,entries.size());position++) {
                Entry e=entries.get(position);
                View item=adapter.getView(position,null,grid);
                GridLayout.LayoutParams bounds=new GridLayout.LayoutParams(
                        GridLayout.spec(position/2),GridLayout.spec(position%2,1f));
                bounds.width=0;
                bounds.height=FinanceUi.dp(requireContext(),48);
                bounds.setMarginEnd(position%2==0 ? FinanceUi.dp(requireContext(),10) : 0);
                bounds.bottomMargin=FinanceUi.dp(requireContext(),10);
                item.setLayoutParams(bounds);
                item.setPadding(0,FinanceUi.dp(requireContext(),4),0,FinanceUi.dp(requireContext(),4));
                fitEntryWidth(item);
                grid.addView(item);
                item.setOnClickListener(v -> FinanceDialogs.entryActions(this,e));
            }
            if(entries.isEmpty()) FinanceUi.text(layValor,"Nenhum lançamento nesta categoria.",14);
            if(entries.size()>5) FinanceUi.text(layValor,"Use os botões acima para visualizar todos os lançamentos.",14);
        }
        FinanceUi.text(content,"Histórico mensal",23);
        YearMonth firstVisibleMonth=state.month.minusMonths(2);
        YearMonth lastVisibleMonth=state.month.plusMonths(2);
        for(Summary s:state.history) {
            YearMonth month=YearMonth.of(s.month.ano,s.month.mes);
            if(month.isBefore(firstVisibleMonth) || month.isAfter(lastVisibleMonth)) continue;
            LinearLayout row=FinanceUi.card(content);
            FinanceUi.text(row,FinanceUi.month(month),18);
            FinanceUi.text(row,"Saldo anterior " + Money.format(s.openingBalanceCents) +
                    "\nReceitas " + Money.format(s.incomeCents()) + "\nDespesas " + Money.format(s.expensesCents()) +
                    "\nResultado do mês " + Money.format(s.netCents()) +
                    "\nSaldo acumulado " + Money.format(s.closingBalanceCents()),14);
            row.setOnClickListener(v -> model.selectMonth(month));
        }
    }
    private void fitEntryWidth(View item) {
        View texts=item.findViewById(R.id.layout_despesa_wrapper_textos);
        ConstraintLayout.LayoutParams textBounds=(ConstraintLayout.LayoutParams)texts.getLayoutParams();
        textBounds.width=0; textBounds.startToEnd=R.id.layout_despesa_wrapper_imagem;
        textBounds.endToEnd=ConstraintLayout.LayoutParams.PARENT_ID;
        texts.setLayoutParams(textBounds);
        TextView name=item.findViewById(R.id.layout_despesa_nome_despesa);
        ConstraintLayout.LayoutParams nameBounds=(ConstraintLayout.LayoutParams)name.getLayoutParams();
        nameBounds.width=0; nameBounds.startToStart=ConstraintLayout.LayoutParams.PARENT_ID;
        nameBounds.endToEnd=ConstraintLayout.LayoutParams.PARENT_ID;
        name.setLayoutParams(nameBounds); name.setMaxLines(1); name.setEllipsize(TextUtils.TruncateAt.END);
    }
}
