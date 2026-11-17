package com.douglasfj.myfinances;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import com.douglasfj.myfinances.models.entities.*;
import com.douglasfj.myfinances.constantes.*;
import org.junit.*;
import org.junit.runner.RunWith;
import java.time.YearMonth;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LedgerRepositoryTest {
    private MyFinancesDatabase db;
    private RoomFinanceRepository repository;
    private final YearMonth november=YearMonth.of(2026,11);
    @Before public void setup() {
        db=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),MyFinancesDatabase.class).build();
        repository=new RoomFinanceRepository(db);
    }
    @After public void close() { db.close(); }

    @Test public void invoicesReplaceInstallmentRowsAndBulkPaymentIsScopedToCardAndMonth() {
        repository.saveCard(null,"A"); repository.saveCard(null,"B");
        State initial=repository.load(november); long a=initial.cards.get(0).idCartao,b=initial.cards.get(1).idCartao;
        repository.savePurchase(null,a,"Compra A1",10000,2,november);
        repository.savePurchase(null,a,"Compra A2",2000,1,november);
        repository.savePurchase(null,b,"Compra B",9000,1,november);
        repository.saveEntry(null,november,"Despesa comum",5000,false,false,false,false);
        State state=repository.load(november);
        assertEquals(3,state.expenses().size()); assertEquals(21000,state.summary().expensesCents());
        assertEquals(2,state.expenses().stream().filter(e -> e.invoiceCardId!=null).count());
        LedgerPage page=repository.ledger(false,"",november,0);
        assertEquals(3,page.rows.size());
        assertEquals(21000,page.rows.stream().mapToLong(e -> Money.cents(e.amount)).sum());
        assertTrue(page.rows.stream().noneMatch(e -> e.name.startsWith("Compra")));
        assertEquals(7000,Money.cents(page.rows.stream().filter(e -> Long.valueOf(a).equals(e.cardId)).findFirst().get().amount));

        repository.setInvoicePaid(a,november,true);
        assertTrue(repository.load(november).invoice(a).isPaid());
        assertFalse(repository.load(november).invoice(b).isPaid());
        assertFalse(repository.load(november.plusMonths(1)).invoice(a).isPaid());
        assertEquals(1,repository.ledger(false,"Fatura A",november,0).rows.get(0).paid);
        Entry common=repository.load(november).entries.stream().filter(e -> e.value.idCompraCartao==null).findFirst().get();
        assertEquals(IndicadorDespesaPaga.NAO_PAGA,common.paid);

        long one=repository.load(november).invoice(a).lines.get(0).entry.linkId;
        repository.setPaid(one,false);
        assertFalse(repository.load(november).invoice(a).isPaid());
        assertEquals(0,repository.ledger(false,"Fatura A",november,0).rows.get(0).paid);
        repository.setInvoicePaid(a,november,true);
        repository.savePurchase(null,a,"Compra adicionada depois",100,1,november);
        assertFalse(repository.load(november).invoice(a).isPaid());
        repository.setInvoicePaid(a,november,false);
        assertTrue(repository.load(november).invoice(a).lines.stream().allMatch(line -> line.entry.paid==IndicadorDespesaPaga.NAO_PAGA));
    }

    @Test public void unpaidZeroValueAlsoMakesInvoicePending() {
        repository.saveCard(null,"Zero"); long card=repository.load(november).cards.get(0).idCartao;
        repository.savePurchase(null,card,"Compra",100,1,november);
        repository.setInvoicePaid(card,november,true);
        long purchase=repository.load(november).purchases.get(0).purchase.idCompraCartao;
        ValoresEntity value=new ValoresEntity("Parcela zerada",0,new Date(),IndicadorValorFixo.NAO_FIXO,IndicadorTipoValor.DESPESA,TipoDespesa.EXTRAS,purchase);
        value.idValor=db.financeiroDAO().insertValue(value);
        db.financeiroDAO().insertLink(new MesesValoresEntity(db.financeiroDAO().month(11,2026).idMes,value.idValor,IndicadorDespesaPaga.NAO_PAGA));
        CardInvoice invoice=repository.load(november).invoice(card);
        assertEquals(0,invoice.pendingCents); assertFalse(invoice.isPaid());
        assertEquals(0,repository.ledger(false,"Fatura",november,0).rows.get(0).paid);
    }

    @Test public void pagesAreLimitedAndSearchAndMonthFiltersRunBeforePagination() {
        for(int i=0;i<35;i++) repository.saveEntry(null,november,"Salário " + i,10000+i,true,false,false,false);
        repository.saveEntry(null,november.plusMonths(1),"Salário dezembro",20000,true,false,false,false);
        repository.saveEntry(null,november,"Despesa",5000,false,false,false,false);
        LedgerPage first=repository.ledger(true,"SALÁRIO",null,0),next=repository.ledger(true,"salário",null,30);
        assertEquals(30,first.rows.size()); assertTrue(first.hasMore);
        assertEquals(6,next.rows.size()); assertFalse(next.hasMore);
        Set<Long> ids=new HashSet<>(); first.rows.forEach(row -> ids.add(row.rowId)); next.rows.forEach(row -> assertTrue(ids.add(row.rowId)));
        assertEquals(36,ids.size());
        LedgerPage december=repository.ledger(true,"salário",november.plusMonths(1),0);
        assertEquals(1,december.rows.size()); assertFalse(december.hasMore);
        assertEquals(30,repository.ledger(true,"",november,0).rows.size());
        assertEquals(0,repository.ledger(false,"Salário",null,0).rows.size());
        assertEquals(0,repository.ledger(true,"inexistente",null,0).rows.size());
    }

    @Test public void searchTreatsWildcardsLiterallyAndEditingUsesTheRowsActualMonth() {
        repository.saveEntry(null,november,"100% extra_1",1000,true,false,false,false);
        repository.saveEntry(null,november.plusMonths(1),"Outra receita",2000,true,false,false,false);
        assertEquals(1,repository.ledger(true,"%",null,0).rows.size());
        assertEquals(1,repository.ledger(true,"_",null,0).rows.size());
        assertEquals(0,repository.ledger(true,"' OR 1=1",null,0).rows.size());
        LedgerRow row=repository.ledger(true,"Outra",null,0).rows.get(0);
        repository.saveEntry(row.valueId,row.period(),"Corrigida",3000,true,false,false,false);
        assertEquals(1000,repository.load(november).summary().incomeCents());
        assertEquals(3000,repository.load(november.plusMonths(1)).summary().incomeCents());
    }
}
