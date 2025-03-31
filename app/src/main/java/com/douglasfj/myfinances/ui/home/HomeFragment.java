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
import com.douglasfj.myfinances.utils.DinheiroUtil;
import com.douglasfj.myfinances.utils.ViewGroupUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setDatabaseContext(getContext());

        setAdapters();
        setListeners();
        setObservers();
        return binding.getRoot();
    }

    private void setListeners() {

    }

    private void setAdapters() {
//      Adapter do grid lista falta pagar
        GridView gridLista = binding.fragmentHomeListaFaltaPagar;

        List<ValoresEntity> listaDespesarFaltaPagar = new ArrayList<>();

        DespesaViewAdapter adapter = new DespesaViewAdapter(getContext(), listaDespesarFaltaPagar);
        gridLista.setAdapter(adapter);

        Disposable subscribe = GlobalState.MES_SELECIONADO
                .subscribeOn(Schedulers.io())
                .subscribe(mesesEntity -> {
                    if (mesesEntity.idMes != null) {
                        List<ValoresEntity> despesasFaltaPagarDoMes = homeViewModel.getAllDespesasFaltaPagarDoMes(mesesEntity.idMes.intValue());
                        adapter.clear();
                        adapter.addAll(despesasFaltaPagarDoMes);
                    }
                });
        compositeDisposable.add(subscribe);
    }

    private void setObservers() {
        Button mesSelecBtn = binding.fragmentHomeMesSelection;
        SensitiveTextView saldoConta = binding.fragmentHomeSaldoConta;
        SensitiveTextView saldoEntrada = binding.fragmentHomeSaldoEntrada;
        SensitiveTextView saldoSaida = binding.fragmentHomeSaldoSaida;

        Disposable subscribe = GlobalState.MES_SELECIONADO.observeOn(Schedulers.io()).subscribe(mesesEntity -> {
            if (mesesEntity != null) {
                mesSelecBtn.setText(mesesEntity.toString());
            }
            if (mesesEntity != null && mesesEntity.idMes != null) {
                double se = homeViewModel.getSaldoEntradaSync(mesesEntity.idMes.intValue());
                double ss = homeViewModel.getSaldoSaidaSync(mesesEntity.idMes.intValue());
                double sc = se - ss;

                saldoConta.setText(DinheiroUtil.floatToStringReais(sc));
                saldoEntrada.setText(DinheiroUtil.floatToStringReais(se));
                saldoSaida.setText(DinheiroUtil.floatToStringReais(ss));
            }
        });
        compositeDisposable.add(subscribe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        homeViewModel = null;
        compositeDisposable.clear();
    }
}
