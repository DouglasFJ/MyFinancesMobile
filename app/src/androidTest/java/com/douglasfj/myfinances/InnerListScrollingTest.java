package com.douglasfj.myfinances;

import android.widget.*;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.douglasfj.myfinances.ui.FinanceUi;
import com.douglasfj.myfinances.ui.MainActivity;
import com.douglasfj.myfinances.ui.shared.ScrollAwareGridView;
import com.douglasfj.myfinances.ui.shared.ScrollAwareListView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class InnerListScrollingTest {
    @Test public void gridAndListScrollInsidePageAndHandOffAtTheEdgeWithoutLosingClicks() {
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            for(boolean grid:new boolean[]{true,false}) {
                ScrollView[] page=new ScrollView[1];
                AbsListView[] inner=new AbsListView[1];
                AtomicBoolean clicked=new AtomicBoolean();
                scenario.onActivity(activity -> {
                    // Isolated views and adapter items: no financial records are inserted.
                    FrameLayout root=new FrameLayout(activity);
                    page[0]=new ScrollView(activity);
                    FrameLayout.LayoutParams bounds=new FrameLayout.LayoutParams(-1,FinanceUi.dp(activity,300));
                    bounds.topMargin=FinanceUi.dp(activity,48);
                    root.addView(page[0],bounds);
                    LinearLayout column=FinanceUi.column(activity); page[0].addView(column);
                    column.addView(new TextView(activity),new LinearLayout.LayoutParams(-1,FinanceUi.dp(activity,250)));
                    inner[0]=grid ? new ScrollAwareGridView(activity) : new ScrollAwareListView(activity);
                    inner[0].setId(R.id.fragment_home_lista_falta_pagar);
                    if(grid) ((GridView)inner[0]).setNumColumns(2);
                    List<String> items=new ArrayList<>();
                    for(int i=0;i<100;i++) items.add("Item " + i);
                    inner[0].setAdapter(new ArrayAdapter<>(activity,android.R.layout.simple_list_item_1,items));
                    inner[0].setOnItemClickListener((parent,view,position,id) -> clicked.set(true));
                    column.addView(inner[0],new LinearLayout.LayoutParams(-1,FinanceUi.dp(activity,150)));
                    column.addView(new TextView(activity),new LinearLayout.LayoutParams(-1,FinanceUi.dp(activity,800)));
                    activity.setContentView(root);
                    page[0].post(() -> page[0].scrollTo(0,FinanceUi.dp(activity,250)));
                });
                onView(withId(R.id.fragment_home_lista_falta_pagar)).check(matches(isDisplayed()));
                onView(withText("Item 0")).perform(click());
                assertTrue("Item clicks still work",clicked.get());
                int[] originalScroll=new int[1];
                scenario.onActivity(activity -> originalScroll[0]=page[0].getScrollY());
                onView(withId(R.id.fragment_home_lista_falta_pagar)).perform(swipeUp());
                scenario.onActivity(activity -> {
                    assertTrue("The inner list must scroll",inner[0].getFirstVisiblePosition()>0);
                    assertEquals("The outer page stays still while the list can scroll",originalScroll[0],page[0].getScrollY());
                    inner[0].smoothScrollBy(0,0);
                    inner[0].setSelection(0);
                });
                onView(withText("Item 0")).check(matches(isDisplayed()));
                onView(withId(R.id.fragment_home_lista_falta_pagar)).perform(swipeDown());
                scenario.onActivity(activity -> assertTrue("At the top edge, the page must scroll",page[0].getScrollY()<originalScroll[0]));
            }
        }
    }
}
