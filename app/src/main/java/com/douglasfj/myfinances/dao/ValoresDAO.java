package com.douglasfj.myfinances.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.douglasfj.myfinances.models.entities.MesesEntity;
import com.douglasfj.myfinances.models.entities.ValorWithMesesValoresRelationship;
import com.douglasfj.myfinances.models.entities.ValoresEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface ValoresDAO {

    @Insert
    Completable insertAll(ValoresEntity... valores);

    @Delete
    void delete(ValoresEntity valor);

    @Update
    int update(ValoresEntity... valores);

    @Query("SELECT * FROM VALORES")
    List<ValoresEntity> getAll();

    @Query("SELECT * FROM VALORES WHERE " +
            "INDICADOR_TIPO_VALOR == 0 AND " +
            "ID_VALOR IN " +
            "(SELECT ID_VALOR FROM MESES_VALORES WHERE ID_MES == :idMes AND INDICADOR_DESPESA_PAGA == 0)")
    List<ValoresEntity> getDepesasNaoPagaMes(int idMes);

    @Query("SELECT SUM(VALOR_REAIS) FROM VALORES WHERE " +
            "INDICADOR_TIPO_VALOR == 1 AND " +
            "ID_VALOR IN " +
            "(SELECT ID_VALOR FROM MESES_VALORES WHERE ID_MES == :idMes)")
    Double getSaldoEntradaMes(int idMes);

    @Query("SELECT SUM(VALOR_REAIS) FROM VALORES WHERE " +
            "INDICADOR_TIPO_VALOR == 0 AND " +
            "ID_VALOR IN " +
            "(SELECT ID_VALOR FROM MESES_VALORES WHERE ID_MES == :idMes)")
    Double getSaldoSaidaMes(int idMes);

    @Query("SELECT * FROM VALORES WHERE ID_VALOR == :idValor")
    ValoresEntity getByID(int idValor);

    @Transaction
    @Query("SELECT * FROM VALORES WHERE ID_VALOR == :idValor")
    List<ValorWithMesesValoresRelationship> getValorWithMesesValores(int idValor);
}
