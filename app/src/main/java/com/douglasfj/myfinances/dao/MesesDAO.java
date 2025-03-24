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

    @Transaction
    @Query("SELECT * FROM MESES WHERE ID_MES == :idMes")
    List<MesWithMesesValoresRelationship> getMesWithMesesValores(int idMes);
}
