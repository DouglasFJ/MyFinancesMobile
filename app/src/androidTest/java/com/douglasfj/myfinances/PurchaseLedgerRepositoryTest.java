package com.douglasfj.myfinances;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.data.*;
import org.junit.*;
import org.junit.runner.RunWith;
import java.time.YearMonth;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PurchaseLedgerRepositoryTest {
    private MyFinancesDatabase db;
    private RoomFinanceRepository repository;
    private final YearMonth december=YearMonth.of(2026,12);
    private long a,b;
    @Before public void setup() {
        db=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),MyFinancesDatabase.class).build();
        repository=new RoomFinanceRepository(db);
        repository.saveCard(null,"A"); repository.saveCard(null,"B");
        a=repository.cards().get(0).idCartao; b=repository.cards().get(1).idCartao;
    }
    @After public void close() { db.close(); }
    @Test public void invoiceMonthReturnsWholePurchaseOnceAndCombinesFilters() {
        repository.savePurchase(null,a,"Compra longa",10000,3,december);
        repository.savePurchase(null,a,"Compra curta",5000,1,december);
        repository.savePurchase(null,b,"Compra outro cartão",9000,2,december);
        assertEquals(3,repository.purchaseLedger("",null,null,0).rows.size());
        LedgerPage january=repository.purchaseLedger("COMPRA",december.plusMonths(1),a,0);
        assertEquals(1,january.rows.size());
        LedgerRow row=january.rows.get(0);
        assertEquals("Compra longa",row.name); assertEquals(10000,Money.cents(row.amount));
        assertEquals(3,row.purchase.purchase.numeroParcelas); assertEquals("A",row.purchase.cardName);
        assertEquals(2,repository.purchaseLedger("",december.plusMonths(1),null,0).rows.size());
        assertTrue(repository.purchaseLedger("curta",december.plusMonths(1),a,0).rows.isEmpty());
        assertTrue(repository.purchaseLedger("longa",december,b,0).rows.isEmpty());
        assertTrue(repository.purchaseLedger("",december.plusMonths(3),null,0).rows.isEmpty());
    }
    @Test public void filteringPrecedesPaginationAndPurchaseIdsDoNotRepeat() {
        for(int i=0;i<35;i++) repository.savePurchase(null,a,"Alvo " + i,1000,2,december);
        repository.savePurchase(null,b,"Alvo outro",1000,1,december);
        LedgerPage first=repository.purchaseLedger("ALVO",december.plusMonths(1),a,0);
        LedgerPage next=repository.purchaseLedger("alvo",december.plusMonths(1),a,30);
        assertEquals(30,first.rows.size()); assertTrue(first.hasMore);
        assertEquals(5,next.rows.size()); assertFalse(next.hasMore);
        Set<Long> ids=new HashSet<>(); first.rows.forEach(row -> ids.add(row.rowId));
        next.rows.forEach(row -> assertTrue(ids.add(row.rowId))); assertEquals(35,ids.size());
        assertEquals(1,repository.purchaseLedger("outro",december,b,0).rows.size());
    }
    @Test public void editingAndDeletingFilteredPurchaseUpdatesAllInstallments() {
        repository.savePurchase(null,a,"Original",10000,3,december);
        LedgerRow row=repository.purchaseLedger("Original",december.plusMonths(1),a,0).rows.get(0);
        repository.savePurchase(row.purchase.purchase.idCompraCartao,a,"Editada",12000,2,december);
        assertEquals(6000,repository.load(december).invoice(a).totalCents);
        assertEquals(6000,repository.load(december.plusMonths(1)).invoice(a).totalCents);
        assertTrue(repository.purchaseLedger("",december.plusMonths(2),a,0).rows.isEmpty());
        repository.deletePurchase(row.purchase.purchase.idCompraCartao);
        assertTrue(repository.purchaseLedger("",null,a,0).rows.isEmpty());
        assertEquals(0,repository.load(december).invoice(a).totalCents);
        assertEquals(0,repository.load(december.plusMonths(1)).invoice(a).totalCents);
    }
}
