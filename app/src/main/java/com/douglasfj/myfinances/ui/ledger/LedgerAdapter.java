package com.douglasfj.myfinances.ui.ledger;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.ui.FinanceUi;
import java.util.*;

final class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.Holder> {
    interface Actions { void edit(LedgerRow row); void delete(LedgerRow row); void pay(LedgerRow row); void more(); }
    private List<LedgerRow> rows=Collections.emptyList();
    private final boolean income, purchases;
    private final Actions actions;
    private boolean more,busy;
    LedgerAdapter(boolean income,boolean purchases,Actions actions) { this.income=income; this.purchases=purchases; this.actions=actions; }
    void submit(LedgerPage page) { rows=page.rows; more=page.hasMore; notifyDataSetChanged(); }
    void busy(boolean busy) { this.busy=busy; notifyDataSetChanged(); }
    @Override public int getItemCount() { return rows.size()+(more ? 1 : 0); }
    @Override public int getItemViewType(int position) { return position==rows.size() ? 1 : 0; }
    static final class Holder extends RecyclerView.ViewHolder {
        final LinearLayout column;
        Holder(View view,LinearLayout column) { super(view); this.column=column; }
    }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent,int type) {
        android.content.Context context=parent.getContext();
        if(type==1) { LinearLayout column=FinanceUi.column(context); return new Holder(column,column); }
        MaterialCardView card=new MaterialCardView(context);
        RecyclerView.LayoutParams params=new RecyclerView.LayoutParams(-1,-2);
        int margin=FinanceUi.dp(context,8); params.setMargins(0,margin,0,margin); card.setLayoutParams(params);
        card.setRadius(FinanceUi.dp(context,16)); card.setCardBackgroundColor(context.getColor(R.color.neutral_50)); card.setStrokeWidth(FinanceUi.dp(context,1));
        card.setStrokeColor(context.getColor(purchases ? R.color.amber_200 : income ? R.color.emerald_200 : R.color.red_200));
        LinearLayout column=FinanceUi.column(context); int padding=FinanceUi.dp(context,16); column.setPadding(padding,padding,padding,padding); card.addView(column);
        return new Holder(card,column);
    }
    @Override public void onBindViewHolder(@NonNull Holder holder,int position) {
        LinearLayout column=holder.column; column.removeAllViews();
        int color=column.getContext().getColor(purchases ? R.color.amber_700 : income ? R.color.emerald_700 : R.color.red_700);
        if(position==rows.size()) {
            MaterialButton button=FinanceUi.button(column,busy ? "Carregando…" : "Ver mais",v -> actions.more());
            button.setId(R.id.ledger_more); button.setBackgroundTintList(ColorStateList.valueOf(color)); button.setEnabled(!busy); return;
        }
        LedgerRow row=rows.get(position);
        TextView name=FinanceUi.text(column,row.name,18); name.setId(R.id.ledger_row_name);
        name.setTextColor(column.getContext().getColor(purchases ? R.color.amber_950 : income ? R.color.emerald_950 : R.color.red_950));
        TextView amount=FinanceUi.text(column,(purchases ? "Valor total: " : "") + Money.format(Money.cents(row.amount)),22); amount.setTextColor(color);
        if(row.purchase != null) {
            FinanceUi.text(column,row.purchase.cardName + " · " + row.purchase.purchase.numeroParcelas + " parcela(s)",14);
            FinanceUi.text(column,"Primeira fatura: " + (row.purchase.purchase.dataPrimeiraParcela == null ? "Não informado" : FinanceUi.month(row.period())),13);
        } else FinanceUi.text(column,FinanceUi.month(row.period()) + " · " + (row.isInvoice() ? "Fatura" : row.fixed==1 ? "Fixo mensal" : "Variável") +
                (row.tax ? " · Imposto" : "") + (income ? "" : row.paid==1 ? " · Paga" : " · Pendente"),13);
        LinearLayout buttons=new LinearLayout(column.getContext()); column.addView(buttons);
        action(buttons,"Editar",R.drawable.pencil,color,() -> actions.edit(row)).setContentDescription("Editar " + row.name);
        action(buttons,"Excluir",R.drawable.trash,color,() -> actions.delete(row)).setContentDescription("Excluir " + row.name);
        if(row.isInvoice()) {
            MaterialButton pay=FinanceUi.button(column,row.paid==1 ? "Marcar fatura pendente" : "Marcar fatura paga",v -> actions.pay(row));
            pay.setBackgroundTintList(ColorStateList.valueOf(color)); pay.setEnabled(!busy);
        }
    }
    private MaterialButton action(LinearLayout parent,String text,int icon,int color,Runnable action) {
        MaterialButton button=new MaterialButton(parent.getContext()); button.setText(text); button.setAllCaps(false);
        button.setIconResource(icon); button.setIconTint(ColorStateList.valueOf(color));
        button.setTextColor(color); button.setStrokeColor(ColorStateList.valueOf(color)); button.setStrokeWidth(FinanceUi.dp(parent.getContext(),1));
        button.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,-2,1); params.setMargins(0,0,FinanceUi.dp(parent.getContext(),6),0);
        parent.addView(button,params); button.setEnabled(!busy); button.setOnClickListener(v -> action.run()); return button;
    }
}
