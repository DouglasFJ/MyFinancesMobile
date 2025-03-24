package com.douglasfj.myfinances.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.douglasfj.myfinances.R;

import java.util.ArrayList;
import java.util.List;

public class ViewGroupUtil {

    public static <T extends View> List<T> getAllViewsOfType(ViewGroup parent, Class<T> classType) {
        List<T> listViews = new ArrayList<>();

        for (int i=0; i<parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getClass().equals(classType)) {
                listViews.add((T) child);
            } else if (child instanceof ViewGroup) {
                listViews.addAll(getAllViewsOfType((ViewGroup) child, classType));
            } else if (child. == R.id.fragment_home_lista_falta_pagar) {
                Log.i("ACHEI ADAPTER", "ADAPTER VIEW");
            }
        }

        return listViews;
    }
}
