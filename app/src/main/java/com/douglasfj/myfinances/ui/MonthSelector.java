package com.douglasfj.myfinances.ui;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.douglasfj.myfinances.R;

/** Shared month navigation for the Home and finance tabs. */
public final class MonthSelector {
    private MonthSelector() {}
    public static void addTo(LinearLayout content, Fragment fragment, java.time.YearMonth month, java.util.function.Consumer<java.time.YearMonth> selected) {
        android.content.Context context=content.getContext();
        ColorStateList selectorBackground = ColorStateList.valueOf(FinanceUi.color(context,R.attr.financeSelectorBackground,
                ContextCompat.getColor(context,R.color.neutral_50)));
        int tamnhoPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());

        LinearLayout row = new LinearLayout(context); content.addView(row);

        Button previous = new Button(context);
        previous.setHeight(20);
        Drawable arrowLeft = ContextCompat.getDrawable(context, R.drawable.arrowleft);

        arrowLeft.setBounds(0, 0, tamnhoPx, tamnhoPx);
        previous.setCompoundDrawables(null, null, arrowLeft, null);
        roundArrowBackground(context,previous,selectorBackground.getDefaultColor(),true);
        previous.setContentDescription("Mês anterior");
        row.addView(previous,new LinearLayout.LayoutParams(FinanceUi.dp(context,48),-2));

        Button current = new Button(context);
        current.setText(FinanceUi.month(month));
        current.setHeight(20);
        current.setBackgroundTintList(
                selectorBackground
        );
        row.addView(current,new LinearLayout.LayoutParams(0,-2,1));

        Button next = new Button(context);
        Drawable arrowRight = ContextCompat.getDrawable(context, R.drawable.arrowright);
        arrowRight.setBounds(0, 0, tamnhoPx, tamnhoPx);
        next.setCompoundDrawables(arrowRight, null, null, null);
        next.setContentDescription("Próximo mês");
        roundArrowBackground(context,next,selectorBackground.getDefaultColor(),false);
        next.setHeight(20);
        row.addView(next,new LinearLayout.LayoutParams(FinanceUi.dp(context,48),-2));

        TypedValue selectorColor=new TypedValue();
        if(context.getTheme().resolveAttribute(R.attr.financeSelectorForeground,selectorColor,true)) {
            int foreground=FinanceUi.color(context,R.attr.financeSelectorForeground,ContextCompat.getColor(context,R.color.neutral_800));
            for(Button button:new Button[]{previous,current,next}) {
                button.setTextColor(foreground);
                button.setCompoundDrawableTintList(ColorStateList.valueOf(foreground));
            }
        }

        previous.setOnClickListener(v -> selected.accept(month.minusMonths(1)));
        next.setOnClickListener(v -> selected.accept(month.plusMonths(1)));
        current.setOnClickListener(v -> FinanceDialogs.pickMonth(fragment,month,selected));
    }
    private static void roundArrowBackground(android.content.Context context,Button button,int color,boolean pointsLeft) {
        float radius=FinanceUi.dp(context,12);
        GradientDrawable shape=new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadii(pointsLeft
                ? new float[]{radius,radius,0,0,0,0,radius,radius}
                : new float[]{0,0,radius,radius,radius,radius,0,0});
        int foreground=FinanceUi.color(context,R.attr.financeSelectorForeground,
                context.getColor(R.color.neutral_800));
        RippleDrawable ripple=new RippleDrawable(ColorStateList.valueOf(
                androidx.core.graphics.ColorUtils.setAlphaComponent(foreground,40)),shape,null);
        button.setBackgroundTintList(null);
        button.setBackground(new InsetDrawable(ripple,FinanceUi.dp(context,3),
                FinanceUi.dp(context,6),FinanceUi.dp(context,3),FinanceUi.dp(context,6)));
        int horizontalPadding=FinanceUi.dp(context,16);
        button.setPadding(horizontalPadding,0,horizontalPadding,0);
    }
}
