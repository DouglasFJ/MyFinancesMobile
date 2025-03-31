package com.douglasfj.myfinances.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.douglasfj.myfinances.constantes.IndicadorDespesaPaga;
import com.douglasfj.myfinances.constantes.IndicadorTipoValor;
import com.douglasfj.myfinances.constantes.IndicadorValorFixo;
import com.douglasfj.myfinances.constantes.TipoDespesa;
import com.douglasfj.myfinances.dao.MesesValoresDAO;
import com.douglasfj.myfinances.dao.ValoresDAO;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.models.entities.MesesValoresEntity;
import com.douglasfj.myfinances.models.entities.ValoresEntity;
import com.douglasfj.myfinances.ui.GlobalState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {

    ValoresDAO valoresDAO;
    MesesValoresDAO mesesValoresDAO;

    LiveData<List<ValoresEntity>> despesasFaltaPagarDoMes = new MutableLiveData<>(new ArrayList<>());

    LiveData<Double> saldoEntrada = new MutableLiveData<>((double) 0);
    LiveData<Double> saldoSaida = new MutableLiveData<>((double) 0);

    public HomeViewModel() {
    }

    void setDatabaseContext(Context context) {
        MyFinancesDatabase database = MyFinancesDatabase.getDatabase(context);
        valoresDAO = database.valoresDAO();
        mesesValoresDAO = database.mesesValoresDAO();


        //FIXME: testes deletar depois
//        GlobalState.MES_SELECIONADO.observeForever(mesesEntity -> {
//            Disposable subscribe = valoresDAO.insertAll(
//                    new ValoresEntity("valor 1", 15.50, new Date(), IndicadorValorFixo.FIXO, IndicadorTipoValor.DESPESA, TipoDespesa.ESSENCIAL, null),
//                    new ValoresEntity("valor 2", 18.50, new Date(), IndicadorValorFixo.NAO_FIXO, IndicadorTipoValor.DESPESA, TipoDespesa.ALIMENTACAO, null),
//                    new ValoresEntity("valor 3", 450.50, new Date(), IndicadorValorFixo.FIXO, IndicadorTipoValor.DESPESA, TipoDespesa.EXTRAS, null)
//            ).subscribeOn(Schedulers.io()).subscribe(() -> {
//                List<ValoresEntity> valores = valoresDAO.getAll();
//
//                valores.forEach(valor -> {
//                    mesesValoresDAO.insertAll(
//                            new MesesValoresEntity(mesesEntity.idMes, valor.idValor, IndicadorDespesaPaga.NAO_PAGA)
//                    );
//                });
//            });
//        });

    }

    List<ValoresEntity> getAllDespesasFaltaPagarDoMes(int idMes) {
        return valoresDAO.getDepesasNaoPagaMes(idMes);
    }

    Double getSaldoEntradaSync(int idMes) {
        return valoresDAO.getSaldoEntradaMes(idMes);
    }

    Double getSaldoSaidaSync(int idMes) {
        return valoresDAO.getSaldoSaidaMes(idMes);
    }

}