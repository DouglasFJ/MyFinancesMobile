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
    private String nameFilter = "", amountFilter = "";

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        Bundle selection = saved != null ? saved : getArguments();
        if (saved != null) { nameFilter = saved.getString("nameFilter", ""); amountFilter = saved.getString("amountFilter", ""); }
        if (selection != null && selection.containsKey("card")) selectedCard = selection.getLong("card");
    }

    @Override public void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        out.putString("nameFilter", nameFilter); out.putString("amountFilter", amountFilter);
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
        EditText name = new EditText(requireContext()); name.setHint("Filtrar por nome"); name.setContentDescription("Filtrar itens pelo nome"); name.setSingleLine(true); name.setText(nameFilter); content.addView(name);
        EditText value = new EditText(requireContext()); value.setHint("Valor exato da parcela"); value.setContentDescription("Filtrar itens pelo valor da parcela"); MoneyInput.configure(value); value.setText(amountFilter); content.addView(value);
        LinearLayout items = FinanceUi.column(requireContext()); content.addView(items);
        Runnable update = () -> {
            nameFilter = name.getText().toString(); amountFilter = value.getText().toString();
            renderItems(items, invoice);
        };
        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            @Override public void onTextChanged(CharSequence s,int start,int before,int count) { update.run(); }
            @Override public void afterTextChanged(android.text.Editable text) {}
        };
        name.addTextChangedListener(watcher); value.addTextChangedListener(watcher);
        renderItems(items, invoice);
    }
    private void renderItems(LinearLayout content, CardInvoice invoice) {
        content.removeAllViews();
        Long cents = null;
        if (!amountFilter.trim().isEmpty()) {
            try { cents = Money.parse(amountFilter); }
            catch (IllegalArgumentException error) { FinanceUi.text(content,"Informe um valor válido para filtrar.",16); return; }
        }
        int matches = 0;
        if (invoice.lines.isEmpty()) FinanceUi.text(content, "Nenhuma parcela para este cartão neste mês.", 16);
        for (CardInvoice.Line line : invoice.lines) {
            if (!normalize(line.name).contains(normalize(nameFilter.trim())) || (cents != null && line.installmentCents != cents)) continue;
            matches++;
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
        if (matches == 0 && !invoice.lines.isEmpty()) FinanceUi.text(content,"Nenhum item encontrado para os filtros informados.",16);
    }
    private static String normalize(String text) {
        return java.text.Normalizer.normalize(text,java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT);
    }
}
