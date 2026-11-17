package com.douglasfj.myfinances;

import android.content.Context;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.ui.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.time.YearMonth;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class InvoiceUiTest {
    @Test public void invoicePageFiltersCardAndShowsPurchaseDetailsAfterRecreation() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        MyFinancesDatabase db=MyFinancesDatabase.getDatabase(context);
        RoomFinanceRepository repository=new RoomFinanceRepository(db);
        String suffix=String.valueOf(System.currentTimeMillis());
        String cardA="Teste fatura A " + suffix, cardB="Teste fatura B " + suffix;
        String purchaseA="Produto A " + suffix, purchaseB="Produto B " + suffix;
        YearMonth month=YearMonth.parse(context.getSharedPreferences("finance",0).getString("month",YearMonth.now().toString()));
        FinanceUiTest.FinanceIdle idle=new FinanceUiTest.FinanceIdle();
        try {
            repository.saveCard(null,cardA); repository.saveCard(null,cardB);
            State state=repository.load(month);
            long idA=state.cards.stream().filter(c -> cardA.equals(c.nomeCartao)).findFirst().get().idCartao;
            long idB=state.cards.stream().filter(c -> cardB.equals(c.nomeCartao)).findFirst().get().idCartao;
            repository.savePurchase(null,idA,purchaseA,10000,1,month);
            repository.savePurchase(null,idB,purchaseB,9000,1,month);
            try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
                scenario.onActivity(activity -> {
                    FinanceViewModel vm=new ViewModelProvider(activity).get(FinanceViewModel.class);
                    vm.state().observe(activity,stateValue -> idle.update(vm));
                    vm.busy().observe(activity,busy -> idle.update(vm));
                });
                IdlingRegistry.getInstance().register(idle);
                onView(withId(R.id.navigation_invoice)).perform(click());
                onView(withId(R.id.invoice_card_selector)).perform(click());
                onData(is(cardA)).perform(click());
                onView(withId(R.id.invoice_total)).check(matches(withText(Money.format(10000))));
                onView(withText("Marcar fatura paga")).perform(scrollTo(),click());
                onIdle();
                scenario.onActivity(activity -> org.junit.Assert.assertTrue(
                        new ViewModelProvider(activity).get(FinanceViewModel.class).state().getValue().invoice(idA).isPaid()));
                onView(withText(containsString("Parcela 1 de 1"))).perform(scrollTo()).check(matches(isDisplayed()));
                onView(withText(purchaseA)).check(matches(isDisplayed()));
                onView(withId(R.id.invoice_card_selector)).perform(scrollTo(),click());
                onData(is(cardB)).perform(click());
                onView(withId(R.id.invoice_total)).check(matches(withText(Money.format(9000))));
                scenario.recreate();
                onView(withId(R.id.invoice_total)).check(matches(withText(Money.format(9000))));
                onView(withText(containsString("Valor total da compra: " + Money.format(9000)))).perform(scrollTo()).check(matches(isDisplayed()));
            }
        } finally {
            IdlingRegistry.getInstance().unregister(idle);
            for(Purchase purchase:db.financeiroDAO().purchases()) {
                if(purchaseA.equals(purchase.purchase.nomeCompra) || purchaseB.equals(purchase.purchase.nomeCompra))
                    repository.deletePurchase(purchase.purchase.idCompraCartao);
            }
            for(com.douglasfj.myfinances.models.entities.CartoesEntity card:db.financeiroDAO().cards()) {
                if(cardA.equals(card.nomeCartao) || cardB.equals(card.nomeCartao)) repository.deleteCard(card.idCartao);
            }
        }
    }
}
