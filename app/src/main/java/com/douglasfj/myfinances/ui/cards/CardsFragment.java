package com.douglasfj.myfinances.ui.cards;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;
import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.ui.*;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import com.douglasfj.myfinances.models.entities.CartoesEntity;
import com.douglasfj.myfinances.constantes.IndicadorDespesaPaga;
import java.time.YearMonth;
import java.time.ZoneId;

public class CardsFragment extends FinancePageFragment {
    private Long selectedCard;
    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved); if(saved!=null && saved.containsKey("card")) selectedCard=saved.getLong("card");
    }
    @Override public void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out); if(selectedCard!=null) out.putLong("card",selectedCard);
    }
    @Override protected void render(LinearLayout content, State state) {
        FinanceUi.text(content,"Cartões",26); monthSelector(content,state);
        FinanceUi.button(content,"Cadastrar cartão",v -> FinanceDialogs.card(this,null));
        if(state.cards.isEmpty()) {
            FinanceUi.text(content,"Cadastre seu primeiro cartão para registrar compras e acompanhar as faturas.",16); return;
        }
        int position=0; String[] names=new String[state.cards.size()];
        for(int i=0;i<names.length;i++) { names[i]=state.cards.get(i).nomeCartao; if(state.cards.get(i).idCartao.equals(selectedCard)) position=i; }
        CartoesEntity card=state.cards.get(position); selectedCard=card.idCartao;
        Spinner spinner=new Spinner(requireContext()); spinner.setContentDescription("Cartão selecionado");
        spinner.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item,names));
        spinner.setSelection(position); content.addView(spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Long next=state.cards.get(pos).idCartao;
                if(!next.equals(selectedCard)) { selectedCard=next; content.removeAllViews(); render(content,state); }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        FinanceUi.button(content,"Gerenciar " + card.nomeCartao,v -> new AlertDialog.Builder(requireContext()).setTitle(card.nomeCartao)
                .setItems(new String[]{"Renomear cartão","Excluir cartão"},(d,w) -> {
                    if(w==0) FinanceDialogs.card(this,card);
                    else FinanceDialogs.confirm(this,"Excluir cartão?","Cartões com compras cadastradas só podem ser excluídos após remover suas compras.",() -> model.deleteCard(card.idCartao));
                }).show());
        FinanceUi.button(content,"Adicionar compra no cartão",v -> FinanceDialogs.purchase(this,null,selectedCard));
        CardInvoice bill=state.invoice(card.idCartao);
        LinearLayout invoice=FinanceUi.card(content); FinanceUi.text(invoice,"Fatura do mês",18);
        FinanceUi.text(invoice,Money.format(bill.totalCents),28); FinanceUi.text(invoice,"Pendente: " + Money.format(bill.pendingCents),16);
        FinanceUi.button(invoice,"Consultar fatura detalhada",v -> {
            Bundle args=new Bundle(); args.putLong("card",card.idCartao);
            NavHostFragment.findNavController(this).navigate(R.id.navigation_invoice,args);
        });
        FinanceUi.text(content,"Parcelas desta fatura",21); int count=0;
        for(Entry e:state.entries) if(belongs(e,state,card.idCartao)) {
            count++; LinearLayout row=FinanceUi.card(content);
            FinanceUi.text(row,e.value.nomeValor + " · " + Money.format(Money.cents(e.value.valorReais)),18);
            FinanceUi.text(row,e.paid==IndicadorDespesaPaga.PAGA ? "Paga · toque para alterar" : "Pendente · toque para pagar",14);
            row.setOnClickListener(v -> FinanceDialogs.entryActions(this,e));
        }
        if(count==0) FinanceUi.text(content,"Nenhuma parcela neste mês.",15);
        FinanceUi.text(content,"Compras cadastradas",21);
        for(Purchase p:state.purchases) if(p.purchase.idCartao==card.idCartao) {
            LinearLayout row=FinanceUi.card(content);
            FinanceUi.text(row,p.purchase.nomeCompra + " · " + Money.format(Money.cents(p.purchase.valorCompra)),18);
            String start=p.purchase.dataPrimeiraParcela==null ? "Não informado" : FinanceUi.month(YearMonth.from(p.purchase.dataPrimeiraParcela.toInstant().atZone(ZoneId.systemDefault())));
            FinanceUi.text(row,p.purchase.numeroParcelas + " parcela(s) · início: " + start,14);
            FinanceUi.button(row,"Editar compra",v -> FinanceDialogs.purchase(this,p,selectedCard));
            FinanceUi.button(row,"Excluir compra",v -> FinanceDialogs.confirm(this,"Excluir compra?","A compra e todas as suas parcelas serão removidas dos meses e das faturas.",() -> model.deletePurchase(p.purchase.idCompraCartao)));
        }
    }
    private boolean belongs(Entry entry, State state, long card) {
        if(entry.value.idCompraCartao==null) return false;
        for(Purchase p:state.purchases) if(p.purchase.idCompraCartao.equals(entry.value.idCompraCartao)) return p.purchase.idCartao==card;
        return false;
    }
}
