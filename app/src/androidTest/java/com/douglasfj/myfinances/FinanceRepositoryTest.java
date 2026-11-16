package com.douglasfj.myfinances;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import org.junit.*;
import org.junit.runner.RunWith;
import java.io.File;
import java.time.YearMonth;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FinanceRepositoryTest {
    private MyFinancesDatabase db;
    private RoomFinanceRepository repository;
    private Context context;
    private final YearMonth november=YearMonth.of(2026,11);
    @Before public void setup() {
        context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        db=Room.inMemoryDatabaseBuilder(context,MyFinancesDatabase.class).build();
        repository=new RoomFinanceRepository(db);
    }
    @After public void close() { db.close(); }
    @Test public void monthTotalsTaxPaymentAndDeletion() {
        repository.saveEntry(null,november,"Salário",500000,true,false,false,false);
        repository.saveEntry(null,november,"Mercado",10000,false,false,false,false);
        repository.saveEntry(null,november,"Imposto",5000,false,true,false,false);
        State state=repository.load(november);
        assertEquals(3,state.entries.size()); Summary summary=state.history.get(0);
        assertEquals(5000,summary.income,0.001); assertEquals(150,summary.expenses,0.001); assertEquals(50,summary.taxes,0.001);
        Entry tax=state.entries.get(0); assertTrue(tax.value.imposto);
        repository.setPaid(tax.linkId,true);
        assertEquals(com.douglasfj.myfinances.constantes.IndicadorDespesaPaga.PAGA,repository.load(november).entries.get(0).paid);
        repository.saveEntry(tax.value.idValor,november,"Imposto corrigido",6000,false,true,false,true);
        assertEquals(160,repository.load(november).history.get(0).expenses,0.001);
        long id=repository.load(november).entries.get(0).value.idValor;
        repository.deleteEntry(id,november);
        assertEquals(100,repository.load(november).history.get(0).expenses,0.001);
        assertEquals(0,repository.load(november.minusMonths(1)).entries.size());
    }
    @Test public void recurringValuesRespectStartEditEndAndHistory() {
        repository.saveEntry(null,november,"Aluguel",100000,false,false,true,false);
        assertEquals(0,repository.load(november.minusMonths(1)).entries.size());
        State december=repository.load(november.plusMonths(1)); assertEquals(1,december.entries.size());
        repository.load(november.plusMonths(1)); assertEquals(1,repository.load(november.plusMonths(1)).entries.size());
        repository.saveEntry(december.entries.get(0).value.idValor,november.plusMonths(1),"Aluguel",110000,false,false,true,false);
        assertEquals(1000,repository.load(november).entries.get(0).value.valorReais,0.001);
        assertEquals(1100,repository.load(november.plusMonths(1)).entries.get(0).value.valorReais,0.001);
        // Editing an earlier revision must not overlap the revised December amount.
        repository.saveEntry(repository.load(november).entries.get(0).value.idValor,november,"Aluguel",95000,false,false,true,false);
        assertEquals(1,repository.load(november.plusMonths(1)).entries.size());
        Entry january=repository.load(YearMonth.of(2027,1)).entries.get(0);
        repository.deleteEntry(january.value.idValor,YearMonth.of(2027,1));
        assertEquals(0,repository.load(YearMonth.of(2027,2)).entries.size());
        assertEquals(1100,repository.load(november.plusMonths(1)).entries.get(0).value.valorReais,0.001);
    }
    @Test public void purchasesSplitAcrossYearsAndCardsWithoutDoubleCounting() {
        repository.saveCard(null,"Principal"); repository.saveCard(null,"Viagens");
        State initial=repository.load(november); long card=initial.cards.get(0).idCartao;
        repository.savePurchase(null,card,"Compra",10000,3,YearMonth.of(2026,12));
        assertEquals(0,repository.load(november).entries.size());
        assertEquals(3334,Money.cents(repository.load(YearMonth.of(2026,12)).entries.get(0).value.valorReais));
        assertEquals(3333,Money.cents(repository.load(YearMonth.of(2027,1)).entries.get(0).value.valorReais));
        assertEquals(3333,Money.cents(repository.load(YearMonth.of(2027,2)).entries.get(0).value.valorReais));
        State january=repository.load(YearMonth.of(2027,1));
        assertNotNull(january.entries.get(0).value.idCompraCartao);
        assertThrows(IllegalArgumentException.class,() -> repository.deleteCard(card));
        long purchase=january.purchases.get(0).purchase.idCompraCartao;
        repository.savePurchase(purchase,card,"Compra alterada",12000,2,november);
        assertEquals(0,repository.load(YearMonth.of(2027,1)).entries.size());
        assertEquals(6000,Money.cents(repository.load(november).entries.get(0).value.valorReais));
        repository.deletePurchase(purchase); assertEquals(0,repository.load(november).entries.size());
        repository.deleteCard(card); assertEquals(1,repository.load(november).cards.size());
    }
    @Test public void invalidPurchaseDoesNotLeavePartialData() {
        repository.saveCard(null,"Cartão"); long card=repository.load(november).cards.get(0).idCartao;
        assertThrows(IllegalArgumentException.class,() -> repository.savePurchase(null,card,"Inválida",2,3,november));
        assertEquals(0,repository.load(november).purchases.size());
        assertEquals(0,repository.load(november).entries.size());
        assertThrows(IllegalArgumentException.class,() -> repository.saveCard(null,"cartão"));
        assertEquals(1,repository.load(november).cards.size());
    }
    @Test public void backupIsStandaloneAndContainsLatestData() throws Exception {
        db.close();
        context.deleteDatabase("backup-repository-test.db");
        db=Room.databaseBuilder(context,MyFinancesDatabase.class,"backup-repository-test.db").build();
        repository=new RoomFinanceRepository(db);
        repository.saveEntry(null,november,"Offline",12345,true,false,false,false);
        db.close();
        db=Room.databaseBuilder(context,MyFinancesDatabase.class,"backup-repository-test.db").build();
        repository=new RoomFinanceRepository(db);
        assertEquals("Offline",repository.load(november).entries.get(0).value.nomeValor);
        File backup=repository.backup(context.getCacheDir());
        try(SQLiteDatabase copy=SQLiteDatabase.openDatabase(backup.getPath(),null,SQLiteDatabase.OPEN_READONLY)) {
            try(android.database.Cursor cursor=copy.rawQuery("SELECT NOME_VALOR, VALOR_REAIS FROM VALORES",null)) {
                assertTrue(cursor.moveToFirst()); assertEquals("Offline",cursor.getString(0)); assertEquals(123.45,cursor.getDouble(1),0.001);
            }
            try(android.database.Cursor integrity=copy.rawQuery("PRAGMA integrity_check",null)) {
                assertTrue(integrity.moveToFirst()); assertEquals("ok",integrity.getString(0));
            }
        } finally { backup.delete(); }
    }
    @Test public void historyIncludesMonthsBetweenRecordsAndFixedPaymentsAreIndependent() {
        repository.saveEntry(null,november,"Internet",10000,false,false,true,false);
        State march=repository.load(YearMonth.of(2027,3));
        assertEquals(5,march.history.size());
        repository.setPaid(march.entries.get(0).linkId,true);
        State february=repository.load(YearMonth.of(2027,2));
        assertEquals(com.douglasfj.myfinances.constantes.IndicadorDespesaPaga.NAO_PAGA,february.entries.get(0).paid);
        assertEquals(100,february.entries.get(0).value.valorReais,0.001);
    }

    @Test public void forecastUpdatesAfterPastEditsWithoutCreatingCarryoverRows() {
        repository.saveEntry(null,november,"Salário",100000,true,false,true,false);
        repository.saveEntry(null,november,"Imposto",10000,false,true,true,false);
        repository.saveEntry(null,november,"Extra",20000,false,false,false,false);
        long extra=repository.load(november).entries.get(0).value.idValor;
        YearMonth january=YearMonth.of(2027,1);
        Summary forecast=repository.load(january).summary();
        assertEquals(160000,forecast.openingBalanceCents);
        assertEquals(250000,forecast.closingBalanceCents());
        assertEquals(100000,forecast.incomeCents());
        assertEquals(3,db.valoresDAO().getAll().size());

        repository.saveEntry(extra,november,"Extra corrigida",40000,false,false,false,false);
        assertEquals(230000,repository.load(january).summary().closingBalanceCents());
        long revised=repository.load(november).entries.get(0).value.idValor;
        repository.deleteEntry(revised,november);
        assertEquals(270000,repository.load(january).summary().closingBalanceCents());
        assertEquals(2,db.valoresDAO().getAll().size());
        assertEquals(2,repository.load(january).entries.size());
    }

    @Test public void invoiceFiltersCardMonthAndExposesPurchaseAndInstallmentDetails() {
        repository.saveCard(null,"A"); repository.saveCard(null,"B");
        State initial=repository.load(november);
        long cardA=initial.cards.get(0).idCartao, cardB=initial.cards.get(1).idCartao;
        repository.savePurchase(null,cardA,"Compra A",10000,3,YearMonth.of(2026,12));
        repository.savePurchase(null,cardB,"Compra B",9000,1,YearMonth.of(2027,1));
        repository.saveEntry(null,YearMonth.of(2027,1),"Despesa avulsa",5000,false,false,false,false);
        State january=repository.load(YearMonth.of(2027,1));
        CardInvoice bill=january.invoice(cardA);
        assertEquals(3333,bill.totalCents);
        assertEquals(1,bill.lines.size());
        assertEquals("Compra A",bill.lines.get(0).name);
        assertEquals(10000,bill.lines.get(0).purchaseCents);
        assertEquals(2,bill.lines.get(0).installmentNumber);
        assertEquals(3,bill.lines.get(0).installmentCount);
        assertEquals(9000,january.invoice(cardB).totalCents);
        assertEquals(0,repository.load(november).invoice(cardA).totalCents);
        assertEquals(3334,repository.load(YearMonth.of(2026,12)).invoice(cardA).totalCents);
        repository.setPaid(bill.lines.get(0).entry.linkId,true);
        CardInvoice paid=repository.load(YearMonth.of(2027,1)).invoice(cardA);
        assertEquals(3333,paid.totalCents); assertEquals(0,paid.pendingCents);
        assertEquals(0,repository.load(YearMonth.of(2027,3)).invoice(cardA).totalCents);
    }
}
