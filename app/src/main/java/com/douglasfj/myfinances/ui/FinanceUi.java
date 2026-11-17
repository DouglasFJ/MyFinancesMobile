package com.douglasfj.myfinances.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.util.TypedValue;
import com.douglasfj.myfinances.R;
import android.widget.*;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FinanceUi {
    private FinanceUi() {}
    public static int color(Context context,int attribute,int fallback) {
        TypedValue value=new TypedValue();
        if(!context.getTheme().resolveAttribute(attribute,value,true)) return fallback;
        return value.resourceId==0 ? value.data : context.getColor(value.resourceId);
    }

    public static int color(Context context, @ColorRes int id) {
        return ContextCompat.getColor(context, id);
    }
    public static int dp(Context context, int size) { return Math.round(size * context.getResources().getDisplayMetrics().density); }
    public static String month(YearMonth month) {
        return month.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", new Locale("pt", "BR")));
    }
    public static LinearLayout column(Context c) {
        LinearLayout layout = new LinearLayout(c); layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2)); return layout;
    }
    public static LinearLayout page(Context c, ScrollView scroll) {
        LinearLayout layout = column(c); int padding = dp(c, 20);
        layout.setPadding(padding, padding, padding, padding); scroll.setFillViewport(true);
        scroll.setBackgroundColor(color(c,R.attr.financePageBackground,Color.rgb(245, 247, 246))); scroll.addView(layout); return layout;
    }
    public static TextView text(LinearLayout parent, String value, int size) {
        TextView text = new TextView(parent.getContext()); text.setText(value); text.setTextSize(size);
        text.setTextColor(color(parent.getContext(),size>=20 ? R.attr.financeHeadingColor : R.attr.financeTextColor,Color.rgb(30, 45, 40)));
        int p = dp(parent.getContext(), 6);
        text.setPadding(0, p, 0, p); parent.addView(text); return text;
    }

    public static TextView textWithColor(LinearLayout parent, String value, int size, @ColorRes int id) {
        TextView text = new TextView(parent.getContext()); text.setText(value); text.setTextSize(size);

        text.setTextColor(
                color(parent.getContext(), id)
        );

        int p = dp(parent.getContext(), 6);
        text.setPadding(0, p, 0, p); parent.addView(text); return text;
    }

    public static MaterialButton button(LinearLayout parent, String label, View.OnClickListener action) {
        MaterialButton button = new MaterialButton(parent.getContext()); button.setText(label); button.setAllCaps(false);
        button.setMinHeight(dp(parent.getContext(), 48));
        button.setBackgroundTintList(ColorStateList.valueOf(color(parent.getContext(),R.attr.financeActionColor,Color.rgb(4, 120, 87)))); button.setTextColor(Color.WHITE);
        parent.addView(button, new LinearLayout.LayoutParams(-1, -2)); button.setOnClickListener(action); return button;
    }
    public static LinearLayout card(LinearLayout parent) {
        Context c = parent.getContext();
        MaterialCardView card = new MaterialCardView(c);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(c, 10), 0, dp(c, 4));
        card.setLayoutParams(params);
        card.setCardBackgroundColor(Color.WHITE);
        card.setRadius(dp(c, 14));
        card.setStrokeWidth(0);
        card.setCardElevation(2);
        int stroke=color(c,R.attr.financeCardStroke,Color.TRANSPARENT);
        if(stroke!=Color.TRANSPARENT) {
            card.setStrokeWidth(dp(c,1));
            card.setStrokeColor(stroke);
        }
        LinearLayout content = column(c);
        int padding = dp(c, 16);
        content.setPadding(padding,padding,padding,padding);
        card.addView(content); parent.addView(card); return content;
    }
    public static ArrayAdapter<String> selectorAdapter(Context context,String[] labels) {
        return new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,labels) {
            @Override public View getView(int position,View convertView,ViewGroup parent) {
                View view=super.getView(position,convertView,parent);
                if(view instanceof TextView) {
                    TextView label=(TextView)view;
                    label.setTextColor(color(context,R.attr.financeSelectorForeground,label.getCurrentTextColor()));
                }
                return view;
            }
        };
    }
}
