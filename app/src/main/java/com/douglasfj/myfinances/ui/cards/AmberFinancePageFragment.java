package com.douglasfj.myfinances.ui.cards;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.ui.FinancePageFragment;

/** Scope the Amber palette to card and invoice screens, including their dialogs. */
public abstract class AmberFinancePageFragment extends FinancePageFragment {
    private Context amberContext;

    @Nullable @Override public Context getContext() {
        Context base=super.getContext();
        if(base==null) return null;
        if(amberContext==null) amberContext=new ContextThemeWrapper(base,R.style.ThemeOverlay_MyFinances_Amber);
        return amberContext;
    }

    @Override public void onDetach() { super.onDetach(); amberContext=null; }
}
