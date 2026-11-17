package com.douglasfj.myfinances.ui.ledger;

import android.content.res.ColorStateList;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.*;
import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.Purchase;
import com.douglasfj.myfinances.ui.*;
import java.time.YearMonth;
import java.util.*;

public class LedgerFragment extends Fragment implements LedgerAdapter.Actions {
    private LedgerViewModel model;
    private LedgerAdapter adapter;
    private Spinner selector;
    private TextView status;
    private TextInputEditText search;
    private boolean income;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;
    private List<YearMonth> months=Collections.emptyList();

    @Override public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup parent,Bundle saved) {
        income=requireActivity().getIntent().getBooleanExtra("income",false);
        model=new ViewModelProvider(requireActivity()).get(LedgerViewModel.class);
        int color=requireContext().getColor(income ? R.color.emerald_700 : R.color.red_700);
        LinearLayout root=FinanceUi.column(requireContext()); root.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
        root.setBackgroundColor(requireContext().getColor(income ? R.color.emerald_50 : R.color.red_50));
        MaterialToolbar toolbar=new MaterialToolbar(requireContext()); toolbar.setTitle(income ? "Receitas" : "Despesas"); toolbar.setTitleTextColor(color);
        android.graphics.drawable.RotateDrawable back=new android.graphics.drawable.RotateDrawable();
        back.setDrawable(androidx.appcompat.content.res.AppCompatResources.getDrawable(requireContext(),R.drawable.arrowright));
        back.setFromDegrees(180); back.setToDegrees(180); back.setLevel(10000);
        android.graphics.drawable.LayerDrawable navigation=new android.graphics.drawable.LayerDrawable(new android.graphics.drawable.Drawable[]{back});
        navigation.setLayerSize(0,FinanceUi.dp(requireContext(),24),FinanceUi.dp(requireContext(),24));
        toolbar.setNavigationIcon(navigation); toolbar.setNavigationIconTint(color); toolbar.setNavigationContentDescription("Voltar");
        toolbar.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        root.addView(toolbar,new LinearLayout.LayoutParams(-1,FinanceUi.dp(requireContext(),56)));
        LinearLayout filters=FinanceUi.column(requireContext()); int padding=FinanceUi.dp(requireContext(),20);
        filters.setPadding(padding,0,padding,0); root.addView(filters);
        TextInputLayout input=new TextInputLayout(requireContext()); input.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        input.setHint(income ? "Buscar receita pelo nome" : "Buscar despesa pelo nome");
        input.setBoxStrokeColor(color); input.setBoxBackgroundColor(android.graphics.Color.WHITE); input.setDefaultHintTextColor(ColorStateList.valueOf(color));
        input.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        input.setEndIconTintList(ColorStateList.valueOf(color));
        search=new TextInputEditText(input.getContext()); search.setId(R.id.ledger_search); search.setSingleLine(true);
        search.setInputType(android.text.InputType.TYPE_CLASS_TEXT); search.setTextColor(requireContext().getColor(R.color.neutral_950));
        search.setContentDescription("Buscar pelo nome"); search.setText(model.search()); input.addView(search,new LinearLayout.LayoutParams(-1,-2)); filters.addView(input);
        FinanceUi.text(filters,"Mês / ano",14).setTextColor(color);
        selector=new Spinner(requireContext()); selector.setId(R.id.ledger_month); selector.setContentDescription("Filtrar mês e ano");
        filters.addView(selector,new LinearLayout.LayoutParams(-1,FinanceUi.dp(requireContext(),48)));
        status=FinanceUi.text(filters,"Carregando…",13); status.setId(R.id.ledger_status);
        RecyclerView list=new RecyclerView(requireContext()); list.setId(R.id.ledger_list); list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setPadding(padding,0,padding,padding); list.setClipToPadding(false);
        adapter=new LedgerAdapter(income,this); list.setAdapter(adapter);
        root.addView(list,new LinearLayout.LayoutParams(-1,0,1)); return root;
    }
    @Override public void onViewCreated(@NonNull View view,@Nullable Bundle saved) {
        model.page().observe(getViewLifecycleOwner(),page -> {
            adapter.submit(page); renderMonths(page);
            status.setText(page.rows.isEmpty() ? (income ? "Nenhuma receita encontrada." : "Nenhuma despesa encontrada.") : "Mostrando " + page.rows.size() + " lançamento(s)");
        });
        model.busy().observe(getViewLifecycleOwner(),busy -> adapter.busy(busy));
        model.message().observe(getViewLifecycleOwner(),event -> { String value=event.consume(); if(value!=null) Toast.makeText(requireContext(),value,Toast.LENGTH_LONG).show(); });
        model.purchases().observe(getViewLifecycleOwner(),event -> { LedgerViewModel.PurchaseSelection value=event.consume(); if(value!=null) choosePurchase(value); });
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            @Override public void onTextChanged(CharSequence s,int start,int before,int count) {
                if(pendingSearch!=null) handler.removeCallbacks(pendingSearch);
                String query=s.toString(); pendingSearch=() -> model.search(query); handler.postDelayed(pendingSearch,300);
            }
            @Override public void afterTextChanged(Editable editable) {}
        }); model.configure(income);
    }
    private void renderMonths(LedgerPage page) {
        List<YearMonth> next=new ArrayList<>(); next.add(null);
        for(int i=page.months.size()-1;i>=0;i--) next.add(YearMonth.of(page.months.get(i).ano,page.months.get(i).mes));
        if(next.equals(months)) return; months=next;
        List<String> labels=new ArrayList<>(); for(YearMonth month:months) labels.add(month==null ? "Todos os meses" : FinanceUi.month(month));
        ArrayAdapter<String> options=new ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,labels) {
            @Override public View getView(int position,View convert,ViewGroup parent) {
                TextView text=(TextView)super.getView(position,convert,parent);
                text.setTextColor(requireContext().getColor(income ? R.color.emerald_800 : R.color.red_800)); return text;
            }
        };
        selector.setAdapter(options); selector.setSelection(Math.max(0,months.indexOf(model.month())));
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent,View view,int position,long id) {
                YearMonth selected=months.get(position); if(!Objects.equals(selected,model.month())) model.month(selected);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    @Override public void edit(LedgerRow row) {
        if(row.isInvoice()) model.invoicePurchases(row,false);
        else FinanceDialogs.entry(this,row.asEntry(income),income,row.period(),model::saveEntry);
    }
    @Override public void delete(LedgerRow row) {
        if(row.isInvoice()) model.invoicePurchases(row,true);
        else FinanceDialogs.confirm(this,"Excluir " + (income ? "receita?" : "despesa?"),
                row.fixed==1 ? "A repetição será encerrada a partir de " + FinanceUi.month(row.period()) + ". Os meses anteriores serão preservados." :
                        "O lançamento de " + FinanceUi.month(row.period()) + " será excluído.",() -> model.delete(row));
    }
    @Override public void pay(LedgerRow row) { FinanceDialogs.invoiceActions(this,row.name.replaceFirst("^Fatura ",""),row.amount,row.paid==1,() -> model.payInvoice(row)); }
    @Override public void more() { model.more(); }
    private void choosePurchase(LedgerViewModel.PurchaseSelection selection) {
        if(selection.purchases.isEmpty()) { Toast.makeText(requireContext(),"Não há compras nesta fatura.",Toast.LENGTH_SHORT).show(); return; }
        String[] labels=new String[selection.purchases.size()]; for(int i=0;i<labels.length;i++) labels[i]=selection.purchases.get(i).purchase.nomeCompra;
        new AlertDialog.Builder(requireContext()).setTitle(selection.delete ? "Excluir compra da fatura" : "Editar compra da fatura")
                .setItems(labels,(dialog,position) -> {
                    Purchase purchase=selection.purchases.get(position);
                    if(selection.delete) FinanceDialogs.confirm(this,"Excluir compra?","A compra e todas as suas parcelas serão excluídas, inclusive de outros meses.",() -> model.deletePurchase(purchase.purchase.idCompraCartao));
                    else FinanceDialogs.purchase(this,purchase,selection.row.cardId,selection.cards,selection.row.period(),model::savePurchase);
                }).setNegativeButton("Cancelar",null).show();
    }
    @Override public void onDestroyView() {
        if(pendingSearch!=null) handler.removeCallbacks(pendingSearch);
        super.onDestroyView(); adapter=null; selector=null; search=null; status=null; months=Collections.emptyList();
    }
}
