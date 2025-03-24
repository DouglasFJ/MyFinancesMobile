package com.douglasfj.myfinances.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.douglasfj.myfinances.adapters.DespesaViewAdapter;
import com.douglasfj.myfinances.constantes.TipoDespesa;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.databinding.FragmentHomeBinding;
import com.douglasfj.myfinances.models.ItemDepesa;
import com.douglasfj.myfinances.models.entities.ValoresEntity;
import com.douglasfj.myfinances.ui.shared.SensitiveTextView;
import com.douglasfj.myfinances.utils.ViewGroupUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MyFinancesDatabase database;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        setListeners();
        setAdapters();
        setObservers();
        return binding.getRoot();
    }

    private void setListeners() {
        ImageButton imageButton = binding.fragmentHomeSensitiveVisibilityButton;
        imageButton.setOnClickListener(view -> {
            MutableLiveData<Boolean> isValuesVisibleLive = homeViewModel.getIsValuesVisible();
            isValuesVisibleLive.setValue(!isValuesVisibleLive.getValue());
        });
    }

    private void setAdapters() {
        GridView gridLista = binding.fragmentHomeListaFaltaPagar;

        ValoresEntity ve = new ValoresEntity();
        ve.nomeValor = "Nome de teste";
        ve.valorReais = 156.45;
        ve.tipoValor = "INVESTIMENTO";
        List<ValoresEntity> listaDespesarFaltaPagar = List.of(ve);

        DespesaViewAdapter adapter = new DespesaViewAdapter(getContext(), listaDespesarFaltaPagar);
        gridLista.setAdapter(adapter);
    }

    private void setObservers() {
        // Pegando todas as SensitivesTextViews para ocultar o valor
        List<SensitiveTextView> sensitiveTextViews = ViewGroupUtil.getAllViewsOfType(binding.getRoot(), SensitiveTextView.class);
        // Ocultando dados das textViews
        homeViewModel.getIsValuesVisible().observe(getViewLifecycleOwner(), aBoolean -> {
            sensitiveTextViews.forEach(v -> {
                v.setSensitiveVisible(aBoolean);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        homeViewModel = null;
        database.close();
    }
}
