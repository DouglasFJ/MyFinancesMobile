package com.douglasfj.myfinances.ui.cards;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.ui.*;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.State;
import com.douglasfj.myfinances.constantes.IndicadorDespesaPaga;

/** Dedicated readout of a card's bill, separate from card/purchase management. */
public class InvoiceFragment extends AmberFinancePageFragment {
    private Long selectedCard;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        Bundle selection = saved != null ? saved : getArguments();
        if (selection != null && selection.containsKey("card")) selectedCard = selection.getLong("card");
    }

    @Override public void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        if (selectedCard != null) out.putLong("card", selectedCard);
    }

    @Override protected void render(LinearLayout content, State state) {
        FinanceUi.text(content, "Faturas", 26);
        FinanceUi.text(content, "Selecione o mês e o cartão para consultar a fatura.", 15);
        monthSelector(content, state);
        if (state.cards.isEmpty()) {
            FinanceUi.text(content, "Cadastre um cartão para consultar suas faturas.", 16);
            FinanceUi.button(content, "Ir para Cartões", v ->
                    NavHostFragment.findNavController(this).navigate(R.id.navigation_card));
            return;
        }

        FinanceUi.text(content, "Cartão", 16);
        int selection = 0;
        String[] labels = new String[state.cards.size()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = state.cards.get(i).nomeCartao;
            if (state.cards.get(i).idCartao.equals(selectedCard)) selection = i;
        }
        selectedCard = state.cards.get(selection).idCartao;
        Spinner selector = new Spinner(requireContext());
        selector.setId(R.id.invoice_card_selector);
        selector.setContentDescription("Cartão da fatura");
        selector.setAdapter(FinanceUi.selectorAdapter(requireContext(),labels));
        selector.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.amber_700)));
        selector.setSelection(selection);
        content.addView(selector, new LinearLayout.LayoutParams(-1, FinanceUi.dp(requireContext(), 56)));
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Long card = state.cards.get(position).idCartao;
                if (!card.equals(selectedCard)) {
                    selectedCard = card;
                    content.removeAllViews();
                    render(content, state);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        CardInvoice invoice = state.invoice(selectedCard);
        LinearLayout total = FinanceUi.card(content);
        FinanceUi.text(total, "Total da fatura · " + labels[selection], 18);
        TextView amount = FinanceUi.text(total, Money.format(invoice.totalCents), 30);
        amount.setId(R.id.invoice_total);
        FinanceUi.text(total, "Pendente: " + Money.format(invoice.pendingCents) +
                "\nPago: " + Money.format(invoice.totalCents - invoice.pendingCents), 16);
        FinanceUi.text(total, "As parcelas desta fatura já estão incluídas nas despesas do mês.", 13);
        if(!invoice.lines.isEmpty()) {
            FinanceUi.text(total,invoice.isPaid() ? "Fatura paga" : "Fatura pendente",16);
            final long cardId=selectedCard;
            FinanceUi.button(total,invoice.isPaid() ? "Marcar fatura pendente" : "Marcar fatura paga",
                    v -> model.payInvoice(cardId,state.month,!invoice.isPaid()));
        }
        FinanceUi.text(content, "Itens da fatura", 22);
        if (invoice.lines.isEmpty()) FinanceUi.text(content, "Nenhuma parcela para este cartão neste mês.", 16);
        for (CardInvoice.Line line : invoice.lines) {
            LinearLayout row = FinanceUi.card(content);
            FinanceUi.text(row, line.name, 19);
            FinanceUi.text(row, "Valor da parcela: " + Money.format(line.installmentCents), 18);
            String number = line.installmentNumber > 0 && line.installmentNumber <= line.installmentCount
                    ? "Parcela " + line.installmentNumber + " de " + line.installmentCount
                    : "Total de parcelas: " + line.installmentCount;
            FinanceUi.text(row, "Valor total da compra: " + Money.format(line.purchaseCents) + "\n" + number, 15);
            FinanceUi.text(row, line.entry.paid == IndicadorDespesaPaga.PAGA
                    ? "Paga · toque para alterar" : "Pendente · toque para registrar pagamento", 14);
            row.setOnClickListener(v -> FinanceDialogs.entryActions(this, line.entry));
        }
    }
}
