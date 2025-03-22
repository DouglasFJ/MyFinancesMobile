package com.douglasfj.myfinances.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
}
