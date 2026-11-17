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
            onView(withId(R.id.ledger_month)).check(matches(withSpinnerText("Todos os meses")));
            onView(withId(R.id.ledger_search)).perform(replaceText(prefix),androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
            onView(withId(R.id.ledger_status)).check(matches(withText("Mostrando 30 lançamento(s)")));
            scenario.onActivity(activity -> ((RecyclerView)activity.findViewById(R.id.ledger_list)).scrollToPosition(30));
            onView(withId(R.id.ledger_more)).perform(click());
            onView(withId(R.id.ledger_status)).check(matches(withText("Mostrando 35 lançamento(s)")));
            onView(withId(R.id.ledger_month)).perform(click()); onData(is(com.douglasfj.myfinances.ui.FinanceUi.month(month))).perform(click());
            onView(withId(R.id.ledger_status)).check(matches(withText("Mostrando 30 lançamento(s)")));
            scenario.recreate(); attach(scenario,idle);
            onView(withId(R.id.ledger_search)).check(matches(withText(prefix)));
            onView(withId(R.id.ledger_month)).check(matches(withSpinnerText(com.douglasfj.myfinances.ui.FinanceUi.month(month))));
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
