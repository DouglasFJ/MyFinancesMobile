package com.douglasfj.myfinances.ui.dashboard;

import android.content.res.ColorStateList;
import android.view.ViewGroup;
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
import androidx.core.content.ContextCompat;

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

        LinearLayout.LayoutParams layoutResumoMes = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GridLayout gridLayoutResumoMes = new GridLayout(requireContext());
        gridLayoutResumoMes.setColumnCount(2);
        gridLayoutResumoMes.setLayoutParams(layoutResumoMes);

        FinanceUi.text(gridLayoutResumoMes, "Saldo do mês:", 18);
        FinanceUi.sensitiveText(gridLayoutResumoMes, Money.format(selected.closingBalanceCents()), 18);;

        FinanceUi.text(gridLayoutResumoMes, "Saldo mês anterior:", 18).setPadding(0, 0, 20, 0);
        FinanceUi.sensitiveText(gridLayoutResumoMes, Money.format(selected.openingBalanceCents), 18);

        FinanceUi.textWithColor(gridLayoutResumoMes, "Receita do mês:", 18, R.color.emerald_800);
        FinanceUi.sensitiveTextWithColor(gridLayoutResumoMes, Money.format(selected.incomeCents()), 18, R.color.emerald_800);

        FinanceUi.textWithColor(gridLayoutResumoMes, "Total receitas:", 18, R.color.emerald_800);
        FinanceUi.sensitiveTextWithColor(gridLayoutResumoMes, Money.format(selected.availableCents()), 18, R.color.emerald_800);

        FinanceUi.textWithColor(gridLayoutResumoMes, "Despesas do mês:", 18, R.color.red_800);
        FinanceUi.sensitiveTextWithColor(gridLayoutResumoMes, Money.format(selected.expensesCents()), 18, R.color.red_800);

        FinanceUi.textWithColor(gridLayoutResumoMes, "Impostos:", 18, R.color.red_800);
        FinanceUi.sensitiveTextWithColor(gridLayoutResumoMes, Money.format(Money.cents(selected.taxes)), 18, R.color.red_800);

        totals.addView(gridLayoutResumoMes);

        LinearLayout.LayoutParams paramsButtons = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsButtons.weight = 1f;
        paramsButtons.setMargins(6, 6, 6, 6);

        LinearLayout lineButtonsReceitas = FinanceUi.line(content);

        FinanceUi.button(lineButtonsReceitas,"Adicionar receita",
                v -> FinanceDialogs.entry(this,null,true),
                paramsButtons
        );
        FinanceUi.button(lineButtonsReceitas,"Visualizar receitas",
                v -> ledger.launch(LedgerActivity.intent(requireContext(),true)),
                paramsButtons
        );

        LinearLayout lineButtonsDespesas = FinanceUi.line(content);

        FinanceUi.button(lineButtonsDespesas,"Adicionar despesa",
                v -> FinanceDialogs.entry(this,null,false),
                paramsButtons
                ).setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(com.douglasfj.myfinances.R.color.red_700)));
        FinanceUi.button(lineButtonsDespesas,"Visualizar despesas",
                v -> ledger.launch(LedgerActivity.intent(requireContext(),false)),
                paramsButtons
                ).setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(com.douglasfj.myfinances.R.color.red_700)));


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
            row.setBackgroundColor(ContextCompat.getColor(content.getContext(), R.color.amber_50));

            FinanceUi.text(row,FinanceUi.month(month),18);

            GridLayout gridLayoutResumoMeses = new GridLayout(requireContext());
            gridLayoutResumoMeses.setColumnCount(2);
            gridLayoutResumoMeses.setLayoutParams(layoutResumoMes);
            row.addView(gridLayoutResumoMeses);

            FinanceUi.text(gridLayoutResumoMeses, "Saldo anterior: ", 12);
            FinanceUi.sensitiveText(gridLayoutResumoMeses, Money.format(s.openingBalanceCents), 12);

            FinanceUi.text(gridLayoutResumoMeses, "Receitas: ", 12);
            FinanceUi.sensitiveText(gridLayoutResumoMeses, Money.format(s.incomeCents()), 12);

            FinanceUi.text(gridLayoutResumoMeses, "Despesas: ", 12);
            FinanceUi.sensitiveText(gridLayoutResumoMeses, Money.format(s.expensesCents()), 12);

            FinanceUi.text(gridLayoutResumoMeses, "Resultado do mês: ", 12);
            FinanceUi.sensitiveText(gridLayoutResumoMeses, Money.format(s.netCents()), 12);

            FinanceUi.text(gridLayoutResumoMeses, "Saldo acumulado: ", 12);
            FinanceUi.sensitiveText(gridLayoutResumoMeses, Money.format(s.closingBalanceCents()), 12);
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
