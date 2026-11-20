package com.douglasfj.myfinances;

import android.content.Context;
import android.text.*;
import android.widget.EditText;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.models.entities.*;
import com.douglasfj.myfinances.constantes.*;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.ui.MainActivity;
import com.douglasfj.myfinances.ui.ledger.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.time.YearMonth;
import java.util.*;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LedgerUiTest {
    static class Idle implements IdlingResource {
        volatile boolean idle;
        ResourceCallback callback;
        @Override public String getName() { return "Ledger search and repository"; }
        @Override public boolean isIdleNow() { return idle; }
        @Override public void registerIdleTransitionCallback(ResourceCallback callback) { this.callback=callback; }
        void update(LedgerViewModel vm,EditText search) {
            idle=vm.page().getValue()!=null && !Boolean.TRUE.equals(vm.busy().getValue()) && search.getText().toString().equals(vm.search());
            if(idle && callback!=null) callback.onTransitionToIdle();
        }
    }
    private void attach(ActivityScenario<LedgerActivity> scenario,Idle idle) {
        scenario.onActivity(activity -> {
            LedgerViewModel vm=new ViewModelProvider(activity).get(LedgerViewModel.class);
            EditText search=activity.findViewById(R.id.ledger_search);
            vm.page().observe(activity,page -> idle.update(vm,search)); vm.busy().observe(activity,busy -> idle.update(vm,search));
            search.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                @Override public void onTextChanged(CharSequence s,int start,int before,int count) { idle.update(vm,search); }
                @Override public void afterTextChanged(Editable editable) {}
            });
        });
    }
    private static void selectMonth(YearMonth month) {
        onView(withId(R.id.ledger_month)).perform(click());
        setPicker("Mês",month.getMonthValue());
        setPicker("Ano",month.getYear());
        onView(withText("Selecionar")).inRoot(isDialog()).perform(click());
    }
    private static void setPicker(String description,int value) {
        onView(withContentDescription(description)).perform(new ViewAction() {
            @Override public org.hamcrest.Matcher<android.view.View> getConstraints() {
                return isAssignableFrom(android.widget.NumberPicker.class);
            }
            @Override public String getDescription() { return "Selecionar " + description; }
            @Override public void perform(UiController controller,android.view.View view) {
                ((android.widget.NumberPicker)view).setValue(value);
                controller.loopMainThreadUntilIdle();
            }
        });
    }
    @Test public void fullScreenSearchPaginationFiltersEditAndDelete() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        MyFinancesDatabase db=MyFinancesDatabase.getDatabase(context);
        String prefix="Lista UI " + System.currentTimeMillis();
        YearMonth month=YearMonth.parse(context.getSharedPreferences("finance",0).getString("month",YearMonth.now().toString()));
        new RoomFinanceRepository(db).load(month);
        long monthId=db.financeiroDAO().month(month.getMonthValue(),month.getYear()).idMes;
        db.runInTransaction(() -> {
            for(int i=0;i<35;i++) {
                ValoresEntity value=new ValoresEntity(prefix+" "+i,100+i,new Date(),IndicadorValorFixo.NAO_FIXO,IndicadorTipoValor.RECEITA,TipoDespesa.EXTRAS,null);
                value.idValor=db.financeiroDAO().insertValue(value);
                db.financeiroDAO().insertLink(new MesesValoresEntity(monthId,value.idValor,null));
            }
        });
        Idle idle=new Idle();
        try(ActivityScenario<LedgerActivity> scenario=ActivityScenario.launch(LedgerActivity.intent(context,true))) {
            onView(withId(R.id.ledger_search)).check(matches(isDisplayed())); attach(scenario,idle); IdlingRegistry.getInstance().register(idle);
            onView(withId(R.id.nav_view)).check(doesNotExist());
            onView(withId(R.id.ledger_month)).check(matches(withText("Todos os meses")));
            onView(withId(R.id.ledger_search)).perform(replaceText(prefix),androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
            onView(withId(R.id.ledger_status)).check(matches(withText("Mostrando 30 lançamento(s)")));
            scenario.onActivity(activity -> ((RecyclerView)activity.findViewById(R.id.ledger_list)).scrollToPosition(30));
            onView(withId(R.id.ledger_more)).perform(click());
            onView(withId(R.id.ledger_status)).check(matches(withText("Mostrando 35 lançamento(s)")));
            selectMonth(month);
            onView(withId(R.id.ledger_status)).check(matches(withText("Mostrando 30 lançamento(s)")));
            scenario.recreate(); attach(scenario,idle);
            onView(withId(R.id.ledger_search)).check(matches(withText(prefix)));
            onView(withId(R.id.ledger_month)).check(matches(withText(com.douglasfj.myfinances.ui.FinanceUi.month(month))));
            scenario.onActivity(activity -> ((RecyclerView)activity.findViewById(R.id.ledger_list)).scrollToPosition(0));
            onView(withContentDescription("Editar "+prefix+" 34")).perform(click());
            onView(withContentDescription("Nome do lançamento")).perform(replaceText(prefix+" editada"));
            onView(withText("Salvar")).inRoot(isDialog()).perform(click());
            onView(withContentDescription("Excluir "+prefix+" editada")).perform(click());
            onView(withText("Confirmar")).inRoot(isDialog()).perform(click()); onIdle();
            assertFalse(db.valoresDAO().getAll().stream().anyMatch(value -> (prefix+" editada").equals(value.nomeValor)));
        } finally {
            IdlingRegistry.getInstance().unregister(idle);
            for(ValoresEntity value:db.valoresDAO().getAll()) if(value.nomeValor!=null && value.nomeValor.startsWith(prefix)) {
                db.financeiroDAO().deleteLinks(value.idValor); db.financeiroDAO().deleteValue(value.idValor);
            }
        }
    }
    @Test public void monthPickerAndFixedFilterPersistForIncomeAndExpenses() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        for(boolean income:new boolean[]{true,false}) {
            Idle idle=new Idle();
            try(ActivityScenario<LedgerActivity> scenario=ActivityScenario.launch(LedgerActivity.intent(context,income))) {
                attach(scenario,idle); IdlingRegistry.getInstance().register(idle);
                onView(withId(R.id.ledger_month)).check(matches(withText("Todos os meses")));
                selectMonth(YearMonth.of(2031,7));
                onView(withId(R.id.ledger_fixed)).perform(click());
                onData(is("Somente fixos")).perform(click());
                scenario.recreate(); attach(scenario,idle);
                onView(withId(R.id.ledger_month)).check(matches(withText(com.douglasfj.myfinances.ui.FinanceUi.month(YearMonth.of(2031,7)))));
                onView(withId(R.id.ledger_fixed)).check(matches(withSpinnerText("Somente fixos")));
                scenario.onActivity(activity -> {
                    LedgerViewModel model=new ViewModelProvider(activity).get(LedgerViewModel.class);
                    assertEquals(Boolean.TRUE,model.fixed());
                    assertEquals(YearMonth.of(2031,7),model.month());
                });
                onView(withId(R.id.ledger_month)).perform(click());
                onView(withText("Todos os meses")).inRoot(isDialog()).perform(click());
                onView(withId(R.id.ledger_month)).check(matches(withText("Todos os meses")));
                onView(withId(R.id.ledger_fixed)).perform(click());
                onData(is("Somente variáveis")).perform(click());
                scenario.onActivity(activity -> assertEquals(Boolean.FALSE,new ViewModelProvider(activity).get(LedgerViewModel.class).fixed()));
            } finally { IdlingRegistry.getInstance().unregister(idle); }
        }
    }
    @Test public void purchasePageFiltersInvoicesAndEditsAndDeletesWholePurchase() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        RoomFinanceRepository repository=new RoomFinanceRepository(MyFinancesDatabase.getDatabase(context));
        String prefix="Compras UI " + System.currentTimeMillis(), cardA=prefix+" A", cardB=prefix+" B";
        YearMonth first=YearMonth.of(2028,12), january=first.plusMonths(1);
        repository.saveCard(null,cardA); repository.saveCard(null,cardB);
        long a=repository.cards().stream().filter(c -> cardA.equals(c.nomeCartao)).findFirst().get().idCartao;
        long b=repository.cards().stream().filter(c -> cardB.equals(c.nomeCartao)).findFirst().get().idCartao;
        repository.savePurchase(null,a,prefix+" longa",10000,3,first);
        repository.savePurchase(null,a,prefix+" curta",5000,1,first);
        repository.savePurchase(null,b,prefix+" outra",9000,2,first);
        Idle idle=new Idle();
        try(ActivityScenario<LedgerActivity> scenario=ActivityScenario.launchActivityForResult(LedgerActivity.purchasesIntent(context,a))) {
            attach(scenario,idle); IdlingRegistry.getInstance().register(idle);
            onView(withId(R.id.nav_view)).check(doesNotExist());
            onView(withId(R.id.ledger_card)).check(matches(withSpinnerText(cardA)));
            onView(withId(R.id.ledger_search)).perform(replaceText(prefix),androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
            onView(withId(R.id.ledger_status)).check(matches(withText("Mostrando 2 compra(s)")));
            selectMonth(january);
            onView(withId(R.id.ledger_status)).check(matches(withText("Mostrando 1 compra(s)")));
            onView(withText("Valor total: " + Money.format(10000))).check(matches(isDisplayed()));
            scenario.recreate(); attach(scenario,idle);
            onView(withId(R.id.ledger_month)).check(matches(withText(com.douglasfj.myfinances.ui.FinanceUi.month(january))));
            onView(withId(R.id.ledger_card)).check(matches(withSpinnerText(cardA)));
            onView(withId(R.id.ledger_search)).check(matches(withText(prefix)));
            scenario.onActivity(activity -> assertEquals(context.getColor(R.color.amber_700),
                    com.douglasfj.myfinances.ui.FinanceUi.color(activity,R.attr.financeActionColor,0)));
            onView(withId(R.id.ledger_card)).perform(click()); onData(is(cardB)).perform(click());
            onView(withContentDescription("Editar "+prefix+" outra")).check(matches(isDisplayed()));
            onView(withId(R.id.ledger_card)).perform(click()); onData(is(cardA)).perform(click());
            onView(withContentDescription("Editar "+prefix+" longa")).perform(click());
            onView(withContentDescription("Nome da compra")).perform(replaceText(prefix+" editada"));
            onView(withContentDescription("Valor total em reais")).perform(replaceText("120,00"),androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
            onView(withText("Salvar")).inRoot(isDialog()).perform(click());
            onView(withContentDescription("Excluir "+prefix+" editada")).perform(click());
            onView(withText("Confirmar")).inRoot(isDialog()).perform(click());
            onView(withId(R.id.ledger_status)).check(matches(withText("Nenhuma compra encontrada.")));
            assertEquals(5000,repository.load(first).invoice(a).totalCents);
            assertEquals(0,repository.load(january).invoice(a).totalCents);
            scenario.onActivity(activity -> activity.getOnBackPressedDispatcher().onBackPressed());
            assertEquals(android.app.Activity.RESULT_OK,scenario.getResult().getResultCode());
        } finally {
            IdlingRegistry.getInstance().unregister(idle);
            for(com.douglasfj.myfinances.data.FinanceModels.Purchase p:MyFinancesDatabase.getDatabase(context).financeiroDAO().purchases())
                if(p.purchase.nomeCompra.startsWith(prefix)) repository.deletePurchase(p.purchase.idCompraCartao);
            repository.deleteCard(a); repository.deleteCard(b);
        }
    }
    @Test public void cardsButtonOpensPurchasesWithoutInlinePurchaseSection() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        RoomFinanceRepository repository=new RoomFinanceRepository(MyFinancesDatabase.getDatabase(context));
        String title="Botão compras " + System.currentTimeMillis(); repository.saveCard(null,title);
        long card=repository.cards().stream().filter(c -> title.equals(c.nomeCartao)).findFirst().get().idCartao;
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.navigation_card)).perform(click());
            onView(withText("Compras cadastradas")).check(doesNotExist());
            onView(withText("Visualizar compras cartões")).perform(scrollTo(),click());
            onView(withText("Compras dos cartões")).check(matches(isDisplayed()));
            onView(withId(R.id.ledger_card)).check(matches(isDisplayed()));
            onView(withId(R.id.nav_view)).check(doesNotExist());
            androidx.test.espresso.Espresso.pressBack();
        } finally { repository.deleteCard(card); }
    }
    @Test public void monthButtonsOpenBothPagesWithoutBottomNavigation() {
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.navigation_month)).perform(click());
            onView(withText("Visualizar receitas")).perform(scrollTo(),click());
            onView(withId(R.id.ledger_search)).check(matches(isDisplayed())); onView(withId(R.id.nav_view)).check(doesNotExist());
            androidx.test.espresso.Espresso.pressBack();
            onView(withText("Visualizar despesas")).perform(scrollTo(),click());
            onView(withId(R.id.ledger_search)).check(matches(isDisplayed())); onView(withId(R.id.nav_view)).check(doesNotExist());
            onView(withText("Despesas")).check(matches(isDisplayed()));
            androidx.test.espresso.Espresso.pressBack();
        }
    }
}
