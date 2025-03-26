package com.douglasfj.myfinances.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.douglasfj.myfinances.models.entities.MesWithMesesValoresRelationship;
import com.douglasfj.myfinances.models.entities.MesesEntity;

import java.util.List;

@Dao
public interface MesesDAO {
    @Insert
    void insertAll(MesesEntity... meses);

    @Delete
    void delete(MesesEntity mes);

    @Update
    int update(MesesEntity... meses);

    @Query("SELECT * FROM MESES")
    List<MesesEntity> getAll();

    @Query("SELECT * FROM MESES WHERE ID_MES == :idMes")
    MesesEntity getByID(int idMes);

    @Query("SELECT * FROM MESES WHERE MES == :mes AND ANO == :ano")
    LiveData<MesesEntity> getByMesAnoAsync(int mes, int ano);

    @Query("SELECT * FROM MESES WHERE MES == :mes AND ANO == :ano")
    MesesEntity getByMesAno(int mes, int ano);

    @Transaction
    @Query("SELECT * FROM MESES WHERE ID_MES == :idMes")
    List<MesWithMesesValoresRelationship> getMesWithMesesValores(int idMes);
}
