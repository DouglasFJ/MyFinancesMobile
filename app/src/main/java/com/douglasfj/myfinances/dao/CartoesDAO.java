package com.douglasfj.myfinances.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.douglasfj.myfinances.models.entities.CartaoWithComprasCartoesRelationship;
import com.douglasfj.myfinances.models.entities.CartoesEntity;

import java.util.List;

@Dao
public interface CartoesDAO {
    @Insert
    void insertAll(CartoesEntity... cartoes);

    @Delete
    void delete(CartoesEntity cartao);

    @Update
    int update(CartoesEntity... cartoes);

    @Query("SELECT * FROM CARTOES")
    List<CartoesEntity> getAll();

    @Query("SELECT * FROM CARTOES WHERE ID_CARTAO == :idCartao")
    CartoesEntity getByID(int idCartao);

    @Transaction
    @Query("SELECT * FROM CARTOES WHERE ID_CARTAO == :idCartao")
    List<CartaoWithComprasCartoesRelationship> getCartaoWithComprasCartoes(int idCartao);
}
