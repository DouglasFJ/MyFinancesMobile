package com.douglasfj.myfinances.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
}
