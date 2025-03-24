package com.douglasfj.myfinances.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.douglasfj.myfinances.models.entities.MesesValoresEntity;

import java.util.List;

@Dao
public interface MesesValoresDAO {
    @Insert
    void insertAll(MesesValoresEntity... mesesValores);

    @Delete
    void delete(MesesValoresEntity mesValor);

    @Update
    int update(MesesValoresEntity... mesesValores);

    @Query("SELECT * FROM MESES_VALORES WHERE ID_MES_VALOR == :idMesValor")
    MesesValoresEntity getByID(int idMesValor);

    @Query("SELECT * FROM MESES_VALORES")
    List<MesesValoresEntity> getAll();
}
