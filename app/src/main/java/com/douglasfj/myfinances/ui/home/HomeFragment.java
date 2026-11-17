package com.douglasfj.myfinances.ui.home;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.douglasfj.myfinances.adapters.DespesaViewAdapter;
import com.douglasfj.myfinances.adapters.UltimasDespesasViewAdapter;
import com.douglasfj.myfinances.databinding.FragmentHomeBinding;
import com.douglasfj.myfinances.data.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import com.douglasfj.myfinances.constantes.*;
import com.douglasfj.myfinances.models.entities.ValoresEntity;
import com.douglasfj.myfinances.ui.*;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FinanceViewModel model;
    private final List<Entry> pending=new ArrayList<>();
    private final List<Entry> recent=new ArrayList<>();
    private DespesaViewAdapter pendingAdapter;
    private UltimasDespesasViewAdapter recentAdapter;

    @Override public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup parent,Bundle saved) {
        binding=FragmentHomeBinding.inflate(inflater,parent,false);
        model=new ViewModelProvider(requireActivity()).get(FinanceViewModel.class);
        pendingAdapter=new DespesaViewAdapter(requireContext(),new ArrayList<>());
        recentAdapter=new UltimasDespesasViewAdapter(requireContext(),new ArrayList<>());
        binding.fragmentHomeListaFaltaPagar.setAdapter(pendingAdapter);
        binding.fragmentHomeListaUltimasDespesas.setAdapter(recentAdapter);
        binding.fragmentHomeMesSelection.setOnClickListener(v -> FinanceDialogs.pickMonth(this,model.month(),model::selectMonth));
        binding.fragmentHomeAddIncome.setOnClickListener(v -> FinanceDialogs.entry(this,null,true));
        binding.fragmentHomeAddExpense.setOnClickListener(v -> FinanceDialogs.entry(this,null,false));
        binding.fragmentHomeListaFaltaPagar.setOnItemClickListener((p,v,pos,id) -> FinanceDialogs.entryActions(this,pending.get(pos)));
        binding.fragmentHomeListaUltimasDespesas.setOnItemClickListener((p,v,pos,id) -> FinanceDialogs.entryActions(this,recent.get(pos)));
        return binding.getRoot();
    }
    @Override public void onViewCreated(@NonNull View view,@Nullable Bundle saved) {
        model.state().observe(getViewLifecycleOwner(),this::render);
    }
    private void render(State state) {
        binding.fragmentHomeMesSelection.setText(FinanceUi.month(state.month));
        pending.clear(); recent.clear();
        List<ValoresEntity> pendingValues=new ArrayList<>(),recentValues=new ArrayList<>();
        for(Entry e:state.expenses()) {
            if(e.value.indicadorTipoValor!=IndicadorTipoValor.RECEITA) {
                if(e.paid!=IndicadorDespesaPaga.PAGA) { pending.add(e); pendingValues.add(e.value); }
                if(recent.size()<10) { recent.add(e); recentValues.add(e.value); }
            }
        }
        Summary summary=state.summary();
        binding.fragmentHomeSaldoConta.setText(Money.format(summary.closingBalanceCents()));
        binding.fragmentHomeSaldoEntrada.setText(Money.format(summary.incomeCents()));
        binding.fragmentHomeSaldoSaida.setText(Money.format(summary.expensesCents()));
        binding.fragmentHomeCarryover.setText("Saldo anterior: " + Money.format(summary.openingBalanceCents) +
                "\nResultado do mês: " + Money.format(summary.netCents()));
        pendingAdapter.clear(); pendingAdapter.addAll(pendingValues);
        recentAdapter.clear(); recentAdapter.addAll(recentValues);
        binding.fragmentHomePendingEmpty.setVisibility(pending.isEmpty() ? View.VISIBLE : View.GONE);
        binding.fragmentHomeRecentEmpty.setVisibility(recent.isEmpty() ? View.VISIBLE : View.GONE);
    }
    @Override public void onDestroyView() {
        super.onDestroyView(); binding=null; pendingAdapter=null; recentAdapter=null; pending.clear(); recent.clear();
    }
}
