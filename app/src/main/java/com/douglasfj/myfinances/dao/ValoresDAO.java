package com.douglasfj.myfinances.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.douglasfj.myfinances.models.entities.ValorWithMesesValoresRelationship;
import com.douglasfj.myfinances.models.entities.ValoresEntity;

import java.util.List;

@Dao
public interface ValoresDAO {

    @Insert
    void insertAll(ValoresEntity... valores);

    @Delete
    void delete(ValoresEntity valor);

    @Update
    int update(ValoresEntity... valores);

    @Query("SELECT * FROM VALORES")
    List<ValoresEntity> getAll();

    @Query("SELECT * FROM VALORES WHERE ID_VALOR == :idValor")
    ValoresEntity getByID(int idValor);

    @Transaction
    @Query("SELECT * FROM VALORES WHERE ID_VALOR == :idValor")
    List<ValorWithMesesValoresRelationship> getValorWithMesesValores(int idValor);
}
