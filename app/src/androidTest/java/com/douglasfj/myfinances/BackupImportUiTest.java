package com.douglasfj.myfinances;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.ui.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.nio.file.Files;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BackupImportUiTest {
    @Test public void warningSurvivesRotationAndCancelPreservesData() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        int previous=MyFinancesDatabase.getDatabase(context).valoresDAO().getAll().size();
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.navigation_backup)).perform(click());
            onView(withText("Importar banco SQLite (.db)")).perform(scrollTo()).check(matches(isDisplayed()));
            scenario.onActivity(activity -> ImportConfirmationDialog.forFile(Uri.parse("content://test/backup.db"),"backup.db")
                    .show(activity.getSupportFragmentManager(),"confirm-database-import"));
            onView(withText(containsString("Todos os dados salvos atualmente neste aplicativo serão apagados"))).check(matches(isDisplayed()));
            onView(withText("Apagar dados e importar")).check(matches(isDisplayed()));
            scenario.recreate();
            onView(withText(containsString("Arquivo: backup.db"))).check(matches(isDisplayed()));
            onView(withId(android.R.id.button2)).perform(click());
            onView(withText("Substituir todos os dados?")).check(doesNotExist());
            scenario.onActivity(activity -> assertFalse(Boolean.TRUE.equals(new ViewModelProvider(activity).get(FinanceViewModel.class).importing().getValue())));
        }
        assertEquals(previous,MyFinancesDatabase.getDatabase(context).valoresDAO().getAll().size());
    }

    @Test public void invalidImportClosesProgressAndLeavesDatabaseUsable() throws Exception {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        int previous=MyFinancesDatabase.getDatabase(context).valoresDAO().getAll().size();
        File invalid=File.createTempFile("invalid-import-", ".db",context.getCacheDir());
        Files.write(invalid.toPath(),new byte[]{1,2,3});
        FinanceUiTest.FinanceIdle idle=new FinanceUiTest.FinanceIdle();
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                FinanceViewModel vm=new ViewModelProvider(activity).get(FinanceViewModel.class);
                vm.state().observe(activity,state -> idle.update(vm));
                vm.busy().observe(activity,busy -> idle.update(vm));
            });
            IdlingRegistry.getInstance().register(idle);
            onIdle();
            scenario.onActivity(activity -> ImportConfirmationDialog.forFile(Uri.fromFile(invalid),"invalid.db")
                    .showNow(activity.getSupportFragmentManager(),"confirm-database-import"));
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
            onIdle();
            onView(withText("Importando banco de dados")).check(doesNotExist());
            scenario.onActivity(activity -> {
                FinanceViewModel vm=new ViewModelProvider(activity).get(FinanceViewModel.class);
                assertFalse(Boolean.TRUE.equals(vm.importing().getValue()));
                vm.refresh();
            });
            onIdle();
        } finally { IdlingRegistry.getInstance().unregister(idle); invalid.delete(); }
        assertEquals(previous,MyFinancesDatabase.getDatabase(context).valoresDAO().getAll().size());
    }
}
