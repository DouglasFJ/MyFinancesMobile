package com.douglasfj.myfinances.data;

import com.douglasfj.myfinances.data.FinanceModels.State;
import java.time.YearMonth;
import java.io.File;
import java.io.IOException;

/** Storage boundary: UI and ViewModel depend on this contract, not Room queries. */
public interface FinanceRepository {
    State load(YearMonth month);
    void saveEntry(Long id, YearMonth month, String name, long cents, boolean income, boolean tax, boolean fixed, boolean paid);
    void deleteEntry(long id, YearMonth fromMonth);
    void setPaid(long linkId, boolean paid);
    void setInvoicePaid(long cardId, YearMonth month, boolean paid);
    LedgerPage ledger(boolean income, String search, YearMonth month, int offset);
    java.util.List<com.douglasfj.myfinances.models.entities.CartoesEntity> cards();
    java.util.List<com.douglasfj.myfinances.data.FinanceModels.Purchase> invoicePurchases(long card,YearMonth month);
    void saveCard(Long id, String name);
    void deleteCard(long id);
    void savePurchase(Long id, long cardId, String name, long cents, int installments, YearMonth firstMonth);
    void deletePurchase(long id);
    File backup(File directory) throws IOException;
}
