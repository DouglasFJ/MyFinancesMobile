package com.douglasfj.myfinances.ui;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
            content.removeAllViews();
            render(content,state);
        });
    }
    protected abstract void render(LinearLayout content, State state);
    protected void monthSelector(LinearLayout content, State state) {
        MonthSelector.addTo(content,this,state.month,model::selectMonth);
    }
    @Override public void onDestroyView() { super.onDestroyView(); content=null; }
}
