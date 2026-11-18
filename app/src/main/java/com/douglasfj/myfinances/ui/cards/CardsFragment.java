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

public class CardsFragment extends AmberFinancePageFragment {
    private Long selectedCard;
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> purchasesLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == android.app.Activity.RESULT_OK) model.refresh();
            });
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
        spinner.setAdapter(FinanceUi.selectorAdapter(requireContext(),names));
        spinner.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.amber_700)));
        spinner.setSelection(position); content.addView(spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Long next=state.cards.get(pos).idCartao;
                if(!next.equals(selectedCard)) { selectedCard=next; content.removeAllViews(); render(content,state); }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        FinanceUi.button(content,"Editar " + card.nomeCartao,v -> new AlertDialog.Builder(requireContext()).setTitle(card.nomeCartao)
                .setItems(new String[]{"Renomear cartão","Excluir cartão"},(d,w) -> {
                    if(w==0) FinanceDialogs.card(this,card);
                    else FinanceDialogs.confirm(this,"Excluir cartão?","Cartões com compras cadastradas só podem ser excluídos após remover suas compras.",() -> model.deleteCard(card.idCartao));
                }).show());
        FinanceUi.button(content,"Adicionar compra no cartão",v -> FinanceDialogs.purchase(this,null,selectedCard));
        FinanceUi.button(content,"Visualizar compras cartões",v -> purchasesLauncher.launch(
                com.douglasfj.myfinances.ui.ledger.LedgerActivity.purchasesIntent(requireContext(),selectedCard)));
        CardInvoice bill=state.invoice(card.idCartao);
        LinearLayout invoice=FinanceUi.card(content); FinanceUi.text(invoice,"Fatura do mês",18);
        FinanceUi.text(invoice,Money.format(bill.totalCents),28); FinanceUi.text(invoice,"Pendente: " + Money.format(bill.pendingCents),16);
        if(!bill.lines.isEmpty()) {
            FinanceUi.text(invoice,bill.isPaid() ? "Fatura paga" : "Fatura pendente",16);
            FinanceUi.button(invoice,bill.isPaid() ? "Marcar fatura pendente" : "Marcar fatura paga",
                    v -> model.payInvoice(card.idCartao,state.month,!bill.isPaid()));
        }
        FinanceUi.button(invoice,"Consultar fatura detalhada",v -> {
            Bundle args=new Bundle(); args.putLong("card",card.idCartao);
            NavHostFragment.findNavController(this).navigate(R.id.navigation_invoice,args);
        });
    }
}
