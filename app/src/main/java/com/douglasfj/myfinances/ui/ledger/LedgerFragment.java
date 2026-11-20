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
    private Button selector;
    private Spinner cardSelector, fixedSelector;
    private List<com.douglasfj.myfinances.models.entities.CartoesEntity> cards=Collections.emptyList();
    private TextView status;
    private TextInputEditText search;
    private boolean income, purchases;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;

    @Override public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup parent,Bundle saved) {
        purchases=requireActivity().getIntent().getBooleanExtra("purchases",false);
        income=requireActivity().getIntent().getBooleanExtra("income",false);
        model=new ViewModelProvider(requireActivity()).get(LedgerViewModel.class);
        int color=requireContext().getColor(purchases ? R.color.amber_700 : income ? R.color.emerald_700 : R.color.red_700);
        LinearLayout root=FinanceUi.column(requireContext()); root.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
        root.setBackgroundColor(requireContext().getColor(purchases ? R.color.amber_50 : income ? R.color.emerald_50 : R.color.red_50));
        MaterialToolbar toolbar=new MaterialToolbar(requireContext()); toolbar.setTitle(purchases ? "Compras dos cartões" : income ? "Receitas" : "Despesas"); toolbar.setTitleTextColor(color);
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
        input.setHint(purchases ? "Buscar compra pelo nome" : income ? "Buscar receita pelo nome" : "Buscar despesa pelo nome");
        input.setBoxStrokeColor(color); input.setBoxBackgroundColor(requireContext().getColor(R.color.neutral_50)); input.setDefaultHintTextColor(ColorStateList.valueOf(color));
        input.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        input.setEndIconTintList(ColorStateList.valueOf(color));
        search=new TextInputEditText(input.getContext()); search.setId(R.id.ledger_search); search.setSingleLine(true);
        search.setInputType(android.text.InputType.TYPE_CLASS_TEXT); search.setTextColor(requireContext().getColor(R.color.neutral_950));
        search.setContentDescription("Buscar pelo nome"); search.setText(model.search()); input.addView(search,new LinearLayout.LayoutParams(-1,-2)); filters.addView(input);
        FinanceUi.text(filters,purchases ? "Mês da fatura / ano" : "Mês / ano",14).setTextColor(color);
        com.google.android.material.button.MaterialButton monthButton=FinanceUi.button(filters,
                model.month()==null ? "Todos os meses" : FinanceUi.month(model.month()),
                v -> FinanceDialogs.pickMonthFilter(this,model.month(),model::month));
        selector=monthButton; selector.setId(R.id.ledger_month); selector.setContentDescription("Filtrar mês e ano");
        monthButton.setIconResource(R.drawable.arrow);
        monthButton.setIconGravity(com.google.android.material.button.MaterialButton.ICON_GRAVITY_END);
        monthButton.setIconTint(ColorStateList.valueOf(requireContext().getColor(R.color.neutral_50)));
        monthButton.setBackgroundTintList(ColorStateList.valueOf(color));
        if(!purchases) {
            FinanceUi.text(filters,"Tipo de lançamento",14).setTextColor(color);
            fixedSelector=new Spinner(requireContext()); fixedSelector.setId(R.id.ledger_fixed);
            fixedSelector.setContentDescription("Filtrar lançamentos fixos ou variáveis");
            fixedSelector.setAdapter(FinanceUi.selectorAdapter(requireContext(),
                    new String[]{"Todos os lançamentos","Somente fixos","Somente variáveis"}));
            fixedSelector.setSelection(model.fixed()==null ? 0 : model.fixed() ? 1 : 2);
            fixedSelector.setBackgroundTintList(ColorStateList.valueOf(color));
            filters.addView(fixedSelector,new LinearLayout.LayoutParams(-1,FinanceUi.dp(requireContext(),48)));
            fixedSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> parent,View view,int position,long id) {
                    model.fixed(position==0 ? null : position==1);
                }
                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
        if(purchases) {
            FinanceUi.text(filters,"Cartão",14).setTextColor(color);
            cardSelector=new Spinner(requireContext()); cardSelector.setId(R.id.ledger_card);
            cardSelector.setContentDescription("Filtrar cartão");
            filters.addView(cardSelector,new LinearLayout.LayoutParams(-1,FinanceUi.dp(requireContext(),48)));
            FinanceUi.text(filters,"O mês filtra compras com parcelas na fatura, exibindo o valor total da compra.",13);
        }
        status=FinanceUi.text(filters,"Carregando…",13); status.setId(R.id.ledger_status);
        RecyclerView list=new RecyclerView(requireContext()); list.setId(R.id.ledger_list); list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setPadding(padding,0,padding,padding); list.setClipToPadding(false);
        adapter=new LedgerAdapter(income,purchases,this); list.setAdapter(adapter);
        root.addView(list,new LinearLayout.LayoutParams(-1,0,1)); return root;
    }
    @Override public void onViewCreated(@NonNull View view,@Nullable Bundle saved) {
        model.page().observe(getViewLifecycleOwner(),page -> {
            adapter.submit(page); selector.setText(model.month()==null ? "Todos os meses" : FinanceUi.month(model.month()));
            if(purchases) renderCards(page);
            status.setText(page.rows.isEmpty() ? (purchases ? "Nenhuma compra encontrada." : income ? "Nenhuma receita encontrada." : "Nenhuma despesa encontrada.") : "Mostrando " + page.rows.size() + (purchases ? " compra(s)" : " lançamento(s)"));
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
        }); model.configure(income,purchases,requireActivity().getIntent().hasExtra("card") ? requireActivity().getIntent().getLongExtra("card",0) : null);
    }
    private void renderCards(LedgerPage page) {
        cards=page.cards;
        List<String> labels=new ArrayList<>(); labels.add("Todos os cartões");
        int selected=0;
        for(int i=0;i<cards.size();i++) {
            labels.add(cards.get(i).nomeCartao);
            if(cards.get(i).idCartao.equals(model.card())) selected=i+1;
        }
        cardSelector.setOnItemSelectedListener(null);
        cardSelector.setAdapter(FinanceUi.selectorAdapter(requireContext(),labels.toArray(new String[0])));
        cardSelector.setSelection(selected);
        cardSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent,View view,int position,long id) {
                model.card(position==0 ? null : cards.get(position-1).idCartao);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    @Override public void edit(LedgerRow row) {
        if(row.purchase!=null) FinanceDialogs.purchase(this,row.purchase,(long)row.purchase.purchase.idCartao,cards,row.period(),model::savePurchase);
        else if(row.isInvoice()) model.invoicePurchases(row,false);
        else FinanceDialogs.entry(this,row.asEntry(income),income,row.period(),model::saveEntry);
    }
    @Override public void delete(LedgerRow row) {
        if(row.purchase!=null) FinanceDialogs.confirm(this,"Excluir compra?","A compra e todas as suas parcelas serão excluídas, inclusive de outros meses.",() -> model.deletePurchase(row.purchase.purchase.idCompraCartao));
        else if(row.isInvoice()) model.invoicePurchases(row,true);
        else FinanceDialogs.deleteEntry(this,row.fixed==1,row.period(),future -> model.delete(row,future));
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
        super.onDestroyView(); adapter=null; selector=null; cardSelector=null; fixedSelector=null; cards=Collections.emptyList(); search=null; status=null;
    }
}
