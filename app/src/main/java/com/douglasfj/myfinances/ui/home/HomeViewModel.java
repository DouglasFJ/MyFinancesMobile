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

    private MyFinancesDatabase db;

    ValoresDAO valoresDAO;
    MesesValoresDAO mesesValoresDAO;

    public HomeViewModel() {
    }

    void setDatabaseContext(Context context) {
        db = MyFinancesDatabase.getDatabase(context);
        valoresDAO = db.valoresDAO();
        mesesValoresDAO = db.mesesValoresDAO();
    }

    void closeDatabase() {
        db.close();
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

    public List<ValoresEntity> getUltimasDespesas(int idMes) {
        return valoresDAO.getUltimasDezDespesas(idMes);
    }


}