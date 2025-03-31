package com.douglasfj.myfinances.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.douglasfj.myfinances.models.entities.MesWithMesesValoresRelationship;
import com.douglasfj.myfinances.models.entities.MesesEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MesesDAO {
    @Insert
    Completable insertAll(MesesEntity... meses);

    @Delete
    Completable delete(MesesEntity mes);

    @Update
    Completable update(MesesEntity... meses);

    @Query("SELECT * FROM MESES")
    List<MesesEntity> getAll();

    @Query("SELECT * FROM MESES WHERE ID_MES == :idMes")
    Single<MesesEntity> getByID(int idMes);

    @Query("SELECT * FROM MESES WHERE MES == :mes AND ANO == :ano")
    Single<MesesEntity> getByMesAno(int mes, int ano);

    @Transaction
    @Query("SELECT * FROM MESES WHERE ID_MES == :idMes")
    List<MesWithMesesValoresRelationship> getMesWithMesesValores(int idMes);
}
