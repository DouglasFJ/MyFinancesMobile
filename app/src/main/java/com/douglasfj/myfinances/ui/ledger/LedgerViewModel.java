package com.douglasfj.myfinances.ui.ledger;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.Purchase;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.models.entities.*;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.*;

/** SQL pagination and mutations run off the UI thread; stale search results are discarded. */
public class LedgerViewModel extends AndroidViewModel {
    public static final class Event<T> {
        private T value;
        Event(T value) { this.value=value; }
        public T consume() { T result=value; value=null; return result; }
    }
    public static final class PurchaseSelection {
        final LedgerRow row;
        final List<CartoesEntity> cards;
        final List<Purchase> purchases;
        final boolean delete;
        PurchaseSelection(LedgerRow row,List<CartoesEntity> cards,List<Purchase> purchases,boolean delete) {
            this.row=row; this.cards=cards; this.purchases=purchases; this.delete=delete;
        }
    }
    private final SavedStateHandle saved;
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private final Handler main=new Handler(Looper.getMainLooper());
    private final MutableLiveData<LedgerPage> page=new MutableLiveData<>();
    private final MutableLiveData<Boolean> busy=new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> changed=new MutableLiveData<>(false);
    private final MutableLiveData<Event<String>> message=new MutableLiveData<>();
    private final MutableLiveData<Event<PurchaseSelection>> purchases=new MutableLiveData<>();
    private final List<LedgerRow> rows=new ArrayList<>();
    private int generation;
    private boolean configured, income, cleared, purchaseMode;
    public LedgerViewModel(@NonNull Application app,SavedStateHandle saved) { super(app); this.saved=saved; }
    public void configure(boolean income) { configure(income,false,null); }
    public void configure(boolean income, boolean purchases, Long initialCard) {
        if(!configured) {
            this.income=income; purchaseMode=purchases; configured=true;
            if(!saved.contains("card")) saved.set("card",initialCard);
            reload();
        }
    }
    public Long card() { return saved.get("card"); }
    public void card(Long card) { if(!Objects.equals(card,card())) { saved.set("card",card); reload(); } }
    public LiveData<LedgerPage> page() { return page; }
    public LiveData<Boolean> busy() { return busy; }
    public LiveData<Boolean> changed() { return changed; }
    public LiveData<Event<String>> message() { return message; }
    public LiveData<Event<PurchaseSelection>> purchases() { return purchases; }
    public String search() { String value=saved.get("search"); return value==null ? "" : value; }
    public YearMonth month() { String value=saved.get("month"); return value==null ? null : YearMonth.parse(value); }
    public Boolean fixed() { return saved.get("fixed"); }
    public void fixed(Boolean fixed) { if(!Objects.equals(fixed,fixed())) { saved.set("fixed",fixed); reload(); } }
    public void search(String search) { if(!search.equals(search())) { saved.set("search",search); reload(); } }
    public void month(YearMonth month) { saved.set("month",month==null ? null : month.toString()); reload(); }
    private FinanceRepository repository() { return new RoomFinanceRepository(MyFinancesDatabase.getDatabase(getApplication())); }
    private void reload() { load(false); }
    public void more() {
        LedgerPage current=page.getValue();
        if(Boolean.TRUE.equals(busy.getValue()) || current==null || !current.hasMore) return;
        load(true);
    }
    private void load(boolean append) {
        int ticket=++generation,offset=append ? rows.size() : 0;
        String query=search(); YearMonth month=month(); Long card=card(); Boolean fixed=fixed();
        busy.setValue(true);
        worker.execute(() -> {
            try {
                LedgerPage next=purchaseMode ? repository().purchaseLedger(query,month,card,offset) : repository().ledger(income,query,month,fixed,offset);
                main.post(() -> {
                    if(cleared || ticket!=generation) return;
                    if(!append) rows.clear();
                    rows.addAll(next.rows);
                    page.setValue(new LedgerPage(new ArrayList<>(rows),next.months,next.hasMore,next.cards));
                    busy.setValue(false);
                });
            } catch(Exception error) { main.post(() -> { if(ticket==generation) { busy.setValue(false); failure(error); } }); }
        });
    }
    private void failure(Exception error) {
        android.util.Log.e("MyFinances","Ledger operation failed",error);
        message.setValue(new Event<>(error instanceof IllegalArgumentException ? error.getMessage() : "Não foi possível carregar ou atualizar os lançamentos. Tente novamente."));
    }
    private void mutate(java.util.function.Consumer<FinanceRepository> action) {
        ++generation; busy.setValue(true);
        worker.execute(() -> {
            try {
                action.accept(repository());
                main.post(() -> { if(!cleared) { changed.setValue(true); reload(); } });
            } catch(Exception error) { main.post(() -> { if(!cleared) { failure(error); reload(); } }); }
        });
    }
    public void saveEntry(Long id,YearMonth month,String name,long cents,boolean income,boolean tax,boolean fixed,boolean paid) {
        mutate(repo -> repo.saveEntry(id,month,name,cents,income,tax,fixed,paid));
    }
    public void saveEntry(Long id,YearMonth month,String name,long cents,boolean income,boolean tax,boolean fixed,boolean paid,boolean future) {
        mutate(repo -> repo.saveEntry(id,month,name,cents,income,tax,fixed,paid,future));
    }
    public void delete(LedgerRow row, boolean future) { mutate(repo -> repo.deleteEntry(row.valueId,row.period(),future)); }
    public void delete(LedgerRow row) { mutate(repo -> repo.deleteEntry(row.valueId,row.period())); }
    public void payInvoice(LedgerRow row) { mutate(repo -> repo.setInvoicePaid(row.cardId,row.period(),row.paid!=1)); }
    public void invoicePurchases(LedgerRow row,boolean delete) {
        worker.execute(() -> {
            try {
                FinanceRepository repository=repository();
                PurchaseSelection selection=new PurchaseSelection(row,repository.cards(),repository.invoicePurchases(row.cardId,row.period()),delete);
                main.post(() -> purchases.setValue(new Event<>(selection)));
            } catch(Exception error) { main.post(() -> failure(error)); }
        });
    }
    public void savePurchase(Long id,long card,String name,long cents,int count,YearMonth first) {
        mutate(repo -> repo.savePurchase(id,card,name,cents,count,first));
    }
    public void deletePurchase(long id) { mutate(repo -> repo.deletePurchase(id)); }
    @Override protected void onCleared() { cleared=true; worker.shutdown(); }
}
