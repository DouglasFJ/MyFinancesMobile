package com.douglasfj.myfinances.ui;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.State;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import java.io.*;
import java.time.YearMonth;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinanceViewModel extends AndroidViewModel {
    public static class Event {
        private final String text;
        private boolean consumed;
        Event(String text) { this.text = text; }
        public String consume() { if (consumed) return null; consumed = true; return text; }
    }
    private FinanceRepository repository;
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final MutableLiveData<State> state = new MutableLiveData<>();
    private final MutableLiveData<Event> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> busy = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> importing = new MutableLiveData<>(false);
    private boolean importInProgress;
    private YearMonth month;
    public FinanceViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomFinanceRepository(MyFinancesDatabase.getDatabase(application));
        String saved = application.getSharedPreferences("finance", 0).getString("month", YearMonth.now().toString());
        try { month = YearMonth.parse(saved); } catch (RuntimeException e) { month = YearMonth.now(); }
        refresh();
    }
    public LiveData<State> state() { return state; }
    public LiveData<Event> message() { return message; }
    public LiveData<Boolean> busy() { return busy; }
    public LiveData<Boolean> importing() { return importing; }
    public YearMonth month() { State current = state.getValue(); return current == null ? month : current.month; }
    public void selectMonth(YearMonth selected) {
        run(() -> {
            month = selected;
            getApplication().getSharedPreferences("finance", 0).edit().putString("month", month.toString()).apply();
        }, null);
    }
    public void refresh() { run(() -> {}, null); }
    private void run(Runnable action, String success) {
        if (importInProgress) {
            message.setValue(new Event("Aguarde a conclusão da importação."));
            return;
        }
        busy.setValue(true);
        worker.execute(() -> {
            try {
                action.run(); state.postValue(repository.load(month));
                if (success != null) message.postValue(new Event(success));
            } catch (Exception e) {
                Log.e("MyFinances", "Operation failed", e);
                message.postValue(new Event(e instanceof IllegalArgumentException ? e.getMessage() : "Não foi possível concluir a operação. Seus dados foram preservados."));
            } finally { busy.postValue(false); }
        });
    }
    public void saveEntry(Long id, YearMonth month, String name, long cents, boolean income, boolean tax, boolean fixed, boolean paid) {
        run(() -> repository.saveEntry(id, month, name, cents, income, tax, fixed, paid), "Lançamento salvo.");
    }
    public void deleteEntry(long id, YearMonth month) { run(() -> repository.deleteEntry(id, month), "Lançamento removido."); }
    public void paid(long id, boolean paid) { run(() -> repository.setPaid(id, paid), "Pagamento atualizado."); }
    public void payInvoice(long card, YearMonth month, boolean paid) {
        run(() -> repository.setInvoicePaid(card,month,paid), "Pagamento da fatura atualizado.");
    }
    public void saveCard(Long id, String name) { run(() -> repository.saveCard(id, name), "Cartão salvo."); }
    public void deleteCard(long id) { run(() -> repository.deleteCard(id), "Cartão removido."); }
    public void savePurchase(Long id, long card, String name, long cents, int count, YearMonth first) {
        run(() -> repository.savePurchase(id, card, name, cents, count, first), "Compra salva e parcelas distribuídas.");
    }
    public void deletePurchase(long id) { run(() -> repository.deletePurchase(id), "Compra e parcelas removidas."); }
    public void export(Uri destination) {
        run(() -> {
            File snapshot = null;
            try {
                snapshot = repository.backup(getApplication().getCacheDir());
                try (InputStream input = new FileInputStream(snapshot);
                     OutputStream output = getApplication().getContentResolver().openOutputStream(destination, "wt")) {
                    if (output == null) throw new IOException("Destino indisponível");
                    byte[] buffer = new byte[8192]; int length;
                    while ((length = input.read(buffer)) != -1) output.write(buffer, 0, length);
                }
            } catch (IOException e) { throw new IllegalStateException(e); }
            finally { if (snapshot != null) snapshot.delete(); }
        }, "Backup SQLite exportado com sucesso.");
    }

    public void importDatabase(Uri source) {
        if (importInProgress) return;
        importInProgress = true;
        importing.setValue(true);
        busy.setValue(true);
        worker.execute(() -> {
            boolean replaced = false;
            try (InputStream input = getApplication().getContentResolver().openInputStream(source)) {
                if (input == null) throw new IOException("Arquivo indisponível.");
                MyFinancesDatabase database = MyFinancesDatabase.importDatabase(getApplication(), input);
                replaced = true;
                repository = new RoomFinanceRepository(database);
                java.util.List<com.douglasfj.myfinances.models.entities.MesesEntity> months = database.financeiroDAO().months();
                boolean selectedExists = months.stream().anyMatch(item -> item.ano == month.getYear() && item.mes == month.getMonthValue());
                if (!selectedExists) {
                    if (months.isEmpty()) month = YearMonth.now();
                    else {
                        com.douglasfj.myfinances.models.entities.MesesEntity last = months.get(months.size() - 1);
                        month = YearMonth.of(last.ano, last.mes);
                    }
                    getApplication().getSharedPreferences("finance", 0).edit().putString("month", month.toString()).apply();
                }
                state.postValue(repository.load(month));
                message.postValue(new Event("Banco importado com sucesso. Os dados anteriores foram substituídos."));
            } catch (Exception error) {
                Log.e("MyFinances", "Database import failed", error);
                try { repository = new RoomFinanceRepository(MyFinancesDatabase.getDatabase(getApplication())); }
                catch (Exception recoveryError) { Log.e("MyFinances", "Database recovery failed", recoveryError); }
                message.postValue(new Event(replaced ? "Banco importado. Reabra o aplicativo para atualizar as telas." :
                        error instanceof IllegalArgumentException ? error.getMessage() :
                                "Não foi possível importar o arquivo. A importação não foi concluída."));
            } finally {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    importInProgress = false;
                    busy.setValue(false);
                    importing.setValue(false);
                });
            }
        });
    }
    @Override protected void onCleared() { worker.shutdown(); }
}
