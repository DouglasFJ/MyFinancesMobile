package com.douglasfj.myfinances;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.douglasfj.myfinances.ui.MainActivity;
import com.douglasfj.myfinances.ui.FinanceViewModel;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.platform.app.InstrumentationRegistry;
import java.time.YearMonth;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FinanceUiTest {
    static class FinanceIdle implements IdlingResource {
        volatile boolean idle;
        ResourceCallback callback;
        @Override public String getName() { return "Finance repository worker"; }
        @Override public boolean isIdleNow() { return idle; }
        @Override public void registerIdleTransitionCallback(ResourceCallback callback) { this.callback=callback; }
        void update(FinanceViewModel vm) {
            idle=vm.state().getValue()!=null && !Boolean.TRUE.equals(vm.busy().getValue());
            if(idle && callback!=null) callback.onTransitionToIdle();
        }
    }
    @Test public void revenueFormPersistsValueAndUpdatesSummaryAfterRecreation() {
        saveRevenueThroughForm("1.234,56",123456);
    }
    @Test public void negativeRevenueFormPersistsSignedAmount() {
        saveRevenueThroughForm("-1234,56",-123456);
    }
    private void saveRevenueThroughForm(String amount,long expectedCents) {
        String title="Teste de interface " + System.currentTimeMillis();
        AtomicReference<YearMonth> selected=new AtomicReference<>();
        FinanceIdle idle=new FinanceIdle();
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                FinanceViewModel vm=new ViewModelProvider(activity).get(FinanceViewModel.class);
                vm.state().observe(activity,state -> { selected.set(state.month); idle.update(vm); });
                vm.busy().observe(activity,busy -> idle.update(vm));
            });
            IdlingRegistry.getInstance().register(idle);
            onView(withId(R.id.fragment_home_add_income)).perform(click());
            onView(withContentDescription("Nome do lançamento")).perform(replaceText(title));
            onView(withContentDescription("Valor em reais")).perform(replaceText(amount),androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
            onView(withText("Salvar")).perform(click());
            onIdle();
            scenario.onActivity(activity -> {
                FinanceViewModel vm=new ViewModelProvider(activity).get(FinanceViewModel.class);
                assertTrue(vm.state().getValue().entries.stream().anyMatch(e -> title.equals(e.value.nomeValor) && Money.cents(e.value.valorReais)==expectedCents));
            });
            IdlingRegistry.getInstance().unregister(idle);
            scenario.recreate();
            scenario.onActivity(activity -> assertEquals(selected.get(),new ViewModelProvider(activity).get(FinanceViewModel.class).month()));
        } finally {
            IdlingRegistry.getInstance().unregister(idle);
            MyFinancesDatabase db=MyFinancesDatabase.getDatabase(InstrumentationRegistry.getInstrumentation().getTargetContext());
            for(com.douglasfj.myfinances.models.entities.ValoresEntity value:db.valoresDAO().getAll()) {
                if(title.equals(value.nomeValor)) new RoomFinanceRepository(db).deleteEntry(value.idValor,selected.get());
            }
        }
    }
    @Test public void navigationAndFormsRemainUsableAfterReturningHome() {
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            for(int i=0;i<3;i++) {
                onView(withId(R.id.navigation_month)).perform(click());
                onView(withText("Histórico mensal")).perform(scrollTo()).check(matches(isDisplayed()));
                onView(withId(R.id.navigation_card)).perform(click());
                onView(withText("Cadastrar cartão")).check(matches(isDisplayed()));
                onView(withId(R.id.navigation_invoice)).perform(click());
                onView(withText("Selecione o mês e o cartão para consultar a fatura.")).check(matches(isDisplayed()));
                onView(withId(R.id.navigation_backup)).perform(click());
                onView(withText("Exportar banco SQLite")).check(matches(isDisplayed()));
                onView(withId(R.id.navigation_home)).perform(click());
                onView(withId(R.id.fragment_home_add_income)).check(matches(isDisplayed()));
            }
            onView(withId(R.id.fragment_home_add_income)).perform(click());
            onView(withText("Salvar")).perform(click());
            onView(withText("Informe um nome de 1 a 120 caracteres.")).check(matches(isDisplayed()));
            onView(withText("Cancelar")).perform(click());
            onView(withId(R.id.fragment_home_add_expense)).perform(click());
            onView(withText("É um imposto")).check(matches(isDisplayed()));
            onView(withText("Cancelar")).perform(click());
        }
    }
}
