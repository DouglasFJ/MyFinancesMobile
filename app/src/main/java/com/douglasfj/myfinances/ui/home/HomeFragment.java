package com.douglasfj.myfinances.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.douglasfj.myfinances.adapters.DespesaViewAdapter;
import com.douglasfj.myfinances.constantes.TipoDespesa;
import com.douglasfj.myfinances.databinding.FragmentHomeBinding;
import com.douglasfj.myfinances.models.ItemDepesa;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        GridView gridLista = binding.fragmentHomeListaFaltaPagar;

        List<ItemDepesa> listaDespesarFaltaPagar = List.of();

        DespesaViewAdapter adapter = new DespesaViewAdapter(getContext(), listaDespesarFaltaPagar);
        gridLista.setAdapter(adapter);

        return binding.getRoot();
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
