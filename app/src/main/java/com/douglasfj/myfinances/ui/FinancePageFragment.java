package com.douglasfj.myfinances.ui;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
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
        roundArrowBackground(previous,selectorBackground.getDefaultColor(),true);
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
        roundArrowBackground(next,selectorBackground.getDefaultColor(),false);
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
    private void roundArrowBackground(Button button,int color,boolean pointsLeft) {
        float radius=FinanceUi.dp(requireContext(),12);
        GradientDrawable shape=new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadii(pointsLeft
                ? new float[]{radius,radius,0,0,0,0,radius,radius}
                : new float[]{0,0,radius,radius,radius,radius,0,0});
        int foreground=FinanceUi.color(requireContext(),R.attr.financeSelectorForeground,
                requireContext().getColor(R.color.neutral_800));
        RippleDrawable ripple=new RippleDrawable(ColorStateList.valueOf(
                androidx.core.graphics.ColorUtils.setAlphaComponent(foreground,40)),shape,null);
        button.setBackgroundTintList(null);
        button.setBackground(new InsetDrawable(ripple,FinanceUi.dp(requireContext(),3),
                FinanceUi.dp(requireContext(),6),FinanceUi.dp(requireContext(),3),FinanceUi.dp(requireContext(),6)));
        int horizontalPadding=FinanceUi.dp(requireContext(),16);
        button.setPadding(horizontalPadding,0,horizontalPadding,0);
    }
    @Override public void onDestroyView() { super.onDestroyView(); content=null; }
}
