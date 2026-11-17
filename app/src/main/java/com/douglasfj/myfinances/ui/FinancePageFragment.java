package com.douglasfj.myfinances.ui;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.data.FinanceModels.State;

public abstract class FinancePageFragment extends Fragment {
    protected FinanceViewModel model;
    private LinearLayout content;
    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle saved) {
        model = new ViewModelProvider(requireActivity()).get(FinanceViewModel.class);
        ScrollView scroll = new ScrollView(requireContext());
        content = FinanceUi.page(requireContext(),scroll);
        FinanceUi.text(content,"Carregando seus dados…",18); return scroll;
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        model.state().observe(getViewLifecycleOwner(),state -> {
            content.removeAllViews(); render(content,state);
        });
    }
    protected abstract void render(LinearLayout content, State state);
    protected void monthSelector(LinearLayout content, State state) {
        ColorStateList selectorBackground = ColorStateList.valueOf(FinanceUi.color(requireContext(),R.attr.financeSelectorBackground,
                ContextCompat.getColor(requireContext(),R.color.neutral_50)));
        int tamnhoPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        LinearLayout row = new LinearLayout(requireContext()); content.addView(row);

        Button previous = new Button(requireContext());
        previous.setHeight(20);
        Drawable arrowLeft = ContextCompat.getDrawable(requireContext(), R.drawable.arrowleft);

        arrowLeft.setBounds(0, 0, tamnhoPx, tamnhoPx);
        previous.setCompoundDrawables(null, null, arrowLeft, null);
        previous.setBackgroundTintList(
                selectorBackground
        );
        previous.setContentDescription("Mês anterior");
        row.addView(previous,new LinearLayout.LayoutParams(FinanceUi.dp(requireContext(),48),-2));

        Button current = new Button(requireContext());
        current.setText(FinanceUi.month(state.month));
        current.setHeight(20);
        current.setBackgroundTintList(
                selectorBackground
        );
        row.addView(current,new LinearLayout.LayoutParams(0,-2,1));

        Button next = new Button(requireContext());
        Drawable arrowRight = ContextCompat.getDrawable(requireContext(), R.drawable.arrowright);
        arrowRight.setBounds(0, 0, tamnhoPx, tamnhoPx);
        next.setCompoundDrawables(arrowRight, null, null, null);
        next.setContentDescription("Próximo mês");
        next.setBackgroundTintList(
                selectorBackground
        );
        next.setHeight(20);
        row.addView(next,new LinearLayout.LayoutParams(FinanceUi.dp(requireContext(),48),-2));

        TypedValue selectorColor=new TypedValue();
        if(requireContext().getTheme().resolveAttribute(R.attr.financeSelectorForeground,selectorColor,true)) {
            int foreground=FinanceUi.color(requireContext(),R.attr.financeSelectorForeground,ContextCompat.getColor(requireContext(),R.color.neutral_800));
            for(Button button:new Button[]{previous,current,next}) {
                button.setTextColor(foreground);
                button.setCompoundDrawableTintList(ColorStateList.valueOf(foreground));
            }
        }

        previous.setOnClickListener(v -> model.selectMonth(state.month.minusMonths(1)));
        next.setOnClickListener(v -> model.selectMonth(state.month.plusMonths(1)));
        current.setOnClickListener(v -> FinanceDialogs.pickMonth(this,state.month,model::selectMonth));
    }
    @Override public void onDestroyView() { super.onDestroyView(); content=null; }
}
