package com.douglasfj.myfinances.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.adapters.DespesaViewAdapter;
import com.douglasfj.myfinances.constantes.TipoDespesa;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.databinding.FragmentHomeBinding;
import com.douglasfj.myfinances.models.ItemDepesa;
import com.douglasfj.myfinances.models.entities.ValoresEntity;
import com.douglasfj.myfinances.ui.GlobalState;
import com.douglasfj.myfinances.ui.shared.SensitiveTextView;
import com.douglasfj.myfinances.utils.ViewGroupUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setAdapters();
        setListeners();
        setObservers();
        return binding.getRoot();
    }

    private void setListeners() {

    }

    private void setAdapters() {
        GridView gridLista = binding.fragmentHomeListaFaltaPagar;


        List<ValoresEntity> listaDespesarFaltaPagar = List.of();

        DespesaViewAdapter adapter = new DespesaViewAdapter(getContext(), listaDespesarFaltaPagar);
        gridLista.setAdapter(adapter);
    }

    private void setObservers() {
        Button mesSelecBtn = binding.fragmentHomeMesSelection;

        GlobalState.MES_SELECIONADO.observe(getViewLifecycleOwner(), mesesEntity -> {
            if (mesesEntity != null) {
                mesSelecBtn.setText(mesesEntity.toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        homeViewModel = null;
    }
}
