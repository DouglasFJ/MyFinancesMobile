package com.douglasfj.myfinances.ui;

import android.text.InputType;
import android.content.Context;
import androidx.appcompat.view.ContextThemeWrapper;
import android.widget.*;
import com.douglasfj.myfinances.R;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.douglasfj.myfinances.constantes.*;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import com.douglasfj.myfinances.models.entities.*;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Consumer;

public final class FinanceDialogs {
    public interface EntrySaver {
        void save(Long id, YearMonth month, String name, long cents, boolean income, boolean tax, boolean fixed, boolean paid, boolean future);
    }
    public interface PurchaseSaver {
        void save(Long id, long card, String name, long cents, int count, YearMonth first);
    }
    private FinanceDialogs() {}
    private static FinanceViewModel model(Fragment f) { return new ViewModelProvider(f.requireActivity()).get(FinanceViewModel.class); }
    public static void pickMonth(Fragment fragment, YearMonth initial, Consumer<YearMonth> selected) {
        pickMonth(fragment.requireContext(),initial,selected);
    }
    private static void pickMonth(Context context, YearMonth initial, Consumer<YearMonth> selected) {
        LinearLayout content = form(context);
        NumberPicker month = new NumberPicker(context); month.setMinValue(1); month.setMaxValue(12);
        month.setDisplayedValues(new String[]{"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"});
        month.setValue(initial.getMonthValue()); month.setContentDescription("Mês"); content.addView(month);
        NumberPicker year = new NumberPicker(context); year.setMinValue(1900); year.setMaxValue(2200);
        year.setValue(initial.getYear()); year.setContentDescription("Ano"); content.addView(year);
        new AlertDialog.Builder(context).setTitle("Selecionar mês").setView(content)
                .setNegativeButton("Cancelar", null).setPositiveButton("Selecionar", (d,w) -> {
                    content.clearFocus(); selected.accept(YearMonth.of(year.getValue(), month.getValue()));
                }).show();
    }
    private static LinearLayout form(Fragment f) {
        return form(f.requireContext());
    }
    private static LinearLayout form(Context context) {
        LinearLayout form = FinanceUi.column(context); int p = FinanceUi.dp(context,20);
        form.setPadding(p,p,p,p); return form;
    }
    private static TextView label(LinearLayout form, String text, int size) {
        TextView label=FinanceUi.text(form,text,size);
        label.setTextColor(FinanceUi.color(form.getContext(),R.attr.financeTextColor,form.getContext().getColor(R.color.dialog_text)));
        return label;
    }
    private static EditText field(LinearLayout form, String title, String value, int type) {
        label(form, title, 14); EditText field = new EditText(form.getContext()); field.setHint(title);
        field.setContentDescription(title); field.setInputType(type); field.setSingleLine(true);
        if ((type & InputType.TYPE_NUMBER_FLAG_DECIMAL) != 0) MoneyInput.configure(field);
        field.setText(value);
        form.addView(field, new LinearLayout.LayoutParams(-1,-2)); return field;
    }
    private static CheckBox check(LinearLayout form, String label, boolean checked) {
        CheckBox check = new CheckBox(form.getContext()); check.setText(label); check.setChecked(checked);
        check.setTextColor(FinanceUi.color(form.getContext(),R.attr.financeTextColor,form.getContext().getColor(R.color.dialog_text)));
        form.addView(check); return check;
    }
    private static void dialog(Fragment f, String title, LinearLayout form, Runnable save) {
        ScrollView scroll = new ScrollView(form.getContext()); scroll.addView(form);
        TextView error = label(form,"",14); error.setTextColor(form.getContext().getColor(R.color.dialog_error));
        error.setAccessibilityLiveRegion(android.view.View.ACCESSIBILITY_LIVE_REGION_POLITE);
        AlertDialog dialog = new AlertDialog.Builder(form.getContext()).setTitle(title).setView(scroll)
                .setNegativeButton("Cancelar",null).setPositiveButton("Salvar",null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try { save.run(); dialog.dismiss(); }
            catch (IllegalArgumentException e) { error.setText(e.getMessage()); }
        })); dialog.show();
    }
    private static String required(EditText field) {
        String value = field.getText().toString().trim();
        if (value.isEmpty() || value.length()>120) throw new IllegalArgumentException("Informe um nome de 1 a 120 caracteres.");
        return value;
    }
    public static void entry(Fragment f, Entry existing, boolean income) {
        FinanceViewModel vm = model(f);
        entry(f,existing,income,vm.month(),vm::saveEntry);
    }
    public static void entry(Fragment f, Entry existing, boolean income, YearMonth initial, EntrySaver save) {
        Context context = new ContextThemeWrapper(f.requireContext(),income
                ? R.style.ThemeOverlay_MyFinances_Entry_Emerald : R.style.ThemeOverlay_MyFinances_Entry_Red);
        LinearLayout form = form(context);
        ValoresEntity value = existing == null ? null : existing.value;
        EditText name = field(form,"Nome do lançamento", value == null ? "" : value.nomeValor, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText amount = field(form,"Valor em reais", value == null ? "" : Money.input(value.valorReais), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        YearMonth[] month = {initial};
        Button monthButton = FinanceUi.button(form,FinanceUi.month(month[0]),v -> {});
        monthButton.setOnClickListener(v -> pickMonth(context,month[0],selected -> {month[0]=selected; monthButton.setText(FinanceUi.month(selected));}));
        if (existing != null) monthButton.setEnabled(false);
        CheckBox tax = check(form,"É um imposto", value != null && value.imposto); tax.setVisibility(income ? android.view.View.GONE : android.view.View.VISIBLE);
        CheckBox fixed = check(form,"Repetir todo mês (valor fixo)", value != null && value.mesInicioFixo != null);
        CheckBox paid = check(form,"Já foi paga", existing != null && existing.paid == IndicadorDespesaPaga.PAGA); paid.setVisibility(income ? android.view.View.GONE : android.view.View.VISIBLE);
        RadioGroup scope = new RadioGroup(context);
        if (value != null && value.mesInicioFixo != null) {
            label(form,"Aplicar alteração a",14);
            RadioButton single = new RadioButton(context); single.setId(android.view.View.generateViewId()); single.setText("Apenas este mês"); scope.addView(single);
            RadioButton following = new RadioButton(context); following.setId(android.view.View.generateViewId()); following.setText("Este mês e os seguintes"); scope.addView(following);
            scope.check(single.getId()); form.addView(scope);
            scope.setOnCheckedChangeListener((group, checked) -> fixed.setEnabled(checked == following.getId()));
            fixed.setEnabled(false);
        }
        label(form,"Valores fixos repetem a partir deste mês. Meses anteriores são preservados.",13);
        dialog(f,existing == null ? (income ? "Nova receita" : "Nova despesa") : "Editar lançamento",form,() ->
                save.save(value == null ? null : value.idValor,month[0],required(name),Money.parse(amount.getText().toString()),income,tax.isChecked(),fixed.isChecked(),paid.isChecked(),scope.getChildCount() == 0 || scope.getCheckedRadioButtonId() == scope.getChildAt(1).getId()));
    }
    public static void entryActions(Fragment f, Entry entry) {
        if(entry.invoiceCardId!=null) {
            invoiceActions(f,entry.cardName,entry.value.valorReais.doubleValue(),entry.paid==IndicadorDespesaPaga.PAGA,
                    () -> model(f).payInvoice(entry.invoiceCardId,entry.invoiceMonth,entry.paid!=IndicadorDespesaPaga.PAGA));
            return;
        }
        FinanceViewModel vm = model(f); boolean income = entry.value.indicadorTipoValor == IndicadorTipoValor.RECEITA;
        boolean card = entry.value.idCompraCartao != null;
        if (card) {
            new AlertDialog.Builder(f.requireContext()).setTitle(entry.value.nomeValor)
                    .setMessage("Parcela do cartão " + entry.cardName + ". Edite ou exclua a compra na aba Cartões.")
                    .setNegativeButton("Fechar",null).setPositiveButton(entry.paid == IndicadorDespesaPaga.PAGA ? "Marcar pendente" : "Marcar paga",
                            (d,w) -> vm.paid(entry.linkId,entry.paid != IndicadorDespesaPaga.PAGA)).show(); return;
        }
        String[] actions = income ? new String[]{"Editar","Excluir"} : new String[]{entry.paid == IndicadorDespesaPaga.PAGA ? "Marcar pendente" : "Marcar paga","Editar","Excluir"};
        new AlertDialog.Builder(f.requireContext()).setTitle(entry.value.nomeValor).setItems(actions,(d,which) -> {
            int action = income ? which + 1 : which;
            if (action==0) vm.paid(entry.linkId, entry.paid != IndicadorDespesaPaga.PAGA);
            else if(action==1) entry(f,entry,income);
            else deleteEntry(f,entry.value.mesInicioFixo != null,vm.month(),future -> vm.deleteEntry(entry.value.idValor,vm.month(),future));
        }).show();
    }
    public static void deleteEntry(Fragment f, boolean fixed, YearMonth month, Consumer<Boolean> delete) {
        if (fixed) {
            new AlertDialog.Builder(f.requireContext()).setTitle("Excluir lançamento de " + FinanceUi.month(month) + "?")
                    .setItems(new String[]{"Apenas este mês", "Este mês e os seguintes"}, (d, which) ->
                            confirm(f,"Excluir lançamento?",which == 0 ? "Somente o lançamento deste mês será excluído." :
                                    "A recorrência será encerrada neste mês. Meses anteriores serão preservados.", () -> delete.accept(which == 1)))
                    .setNegativeButton("Cancelar",null).show();
        } else confirm(f,"Excluir lançamento?","Este lançamento será removido do mês.",() -> delete.accept(true));
    }
    public static void invoiceActions(Fragment f,String card,double total,boolean paid,Runnable changePayment) {
        new AlertDialog.Builder(f.requireContext()).setTitle("Fatura " + card)
                .setMessage(Money.format(Money.cents(total)) + "\n" + (paid ? "Paga" : "Pendente") +
                        "\n\nA alteração será aplicada a todas as parcelas deste cartão no mês selecionado.")
                .setNegativeButton("Fechar",null).setPositiveButton(paid ? "Marcar fatura pendente" : "Marcar fatura paga",(d,w) -> changePayment.run()).show();
    }
    public static void confirm(Fragment f, String title, String message, Runnable action) {
        new AlertDialog.Builder(f.requireContext()).setTitle(title).setMessage(message).setNegativeButton("Cancelar",null)
                .setPositiveButton("Confirmar",(d,w) -> action.run()).show();
    }
    public static void card(Fragment f, CartoesEntity card) {
        LinearLayout form = form(f); EditText name = field(form,"Nome do cartão",card == null ? "" : card.nomeCartao,InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        dialog(f,card == null ? "Novo cartão" : "Renomear cartão",form,() -> model(f).saveCard(card == null ? null : card.idCartao,required(name)));
    }
    public static void purchase(Fragment f, Purchase existing, Long preferredCard) {
        FinanceViewModel vm = model(f); State state = vm.state().getValue();
        if (state == null || state.cards.isEmpty()) { card(f,null); return; }
        purchase(f,existing,preferredCard,state.cards,vm.month(),vm::savePurchase);
    }
    public static void purchase(Fragment f, Purchase existing, Long preferredCard,List<CartoesEntity> cards,YearMonth initial,PurchaseSaver save) {
        ComprasCartoesEntity purchase = existing == null ? null : existing.purchase;
        LinearLayout form = form(f);
        EditText name = field(form,"Nome da compra",purchase == null ? "" : purchase.nomeCompra,InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText amount = field(form,"Valor total em reais",purchase == null ? "" : Money.input(purchase.valorCompra),InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText installments = field(form,"Número de parcelas (1 a 120)",purchase == null ? "1" : String.valueOf(purchase.numeroParcelas),InputType.TYPE_CLASS_NUMBER);
        label(form,"Cartão",14); Spinner spinner = new Spinner(f.requireContext());
        String[] labels = new String[cards.size()]; int selection = 0;
        long target = purchase != null ? purchase.idCartao : preferredCard == null ? -1 : preferredCard;
        for(int i=0;i<cards.size();i++) { labels[i]=cards.get(i).nomeCartao; if(cards.get(i).idCartao==target) selection=i; }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(f.requireContext(),android.R.layout.simple_spinner_dropdown_item,labels);
        spinner.setAdapter(adapter); spinner.setSelection(selection); spinner.setContentDescription("Cartão da compra"); form.addView(spinner);
        YearMonth[] first = {purchase == null || purchase.dataPrimeiraParcela == null ? initial : YearMonth.from(purchase.dataPrimeiraParcela.toInstant().atZone(ZoneId.systemDefault()))};
        label(form,"Mês da primeira parcela",14);
        Button month = FinanceUi.button(form,FinanceUi.month(first[0]),v -> {});
        month.setOnClickListener(v -> pickMonth(f,first[0],selected -> {first[0]=selected; month.setText(FinanceUi.month(selected));}));
        label(form,"O valor total será dividido em parcelas mensais. Eventuais centavos serão distribuídos nas primeiras parcelas.",13);
        if(purchase!=null) label(form,"Ao editar, todas as parcelas serão recalculadas e marcadas como pendentes.",13);
        dialog(f,purchase == null ? "Nova compra no cartão" : "Editar compra",form,() -> {
            int count;
            try { count=Integer.parseInt(installments.getText().toString()); } catch(NumberFormatException e) { throw new IllegalArgumentException("Informe de 1 a 120 parcelas."); }
            long cents = Money.parse(amount.getText().toString()); Money.split(cents,count);
            save.save(purchase == null ? null : purchase.idCompraCartao,cards.get(spinner.getSelectedItemPosition()).idCartao,required(name),cents,count,first[0]);
        });
    }
}
