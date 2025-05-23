package com.douglasfj.myfinances.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.douglasfj.myfinances.models.entities.CompraCartaoWithValoresRelationship;
import com.douglasfj.myfinances.models.entities.ComprasCartoesEntity;

import java.util.List;

@Dao
public interface ComprasCartoesDAO {
    @Insert
    void insertAll(ComprasCartoesEntity... comprasCartoes);

    @Delete
    void delete(ComprasCartoesEntity compraCartao);

    @Update
    int update(ComprasCartoesEntity... comprasCartoes);

    @Query("SELECT * FROM COMPRAS_CARTOES")
    List<ComprasCartoesEntity> getAll();

    @Query("SELECT * FROM COMPRAS_CARTOES WHERE ID_COMPRA_CARTAO == :idCompraCartao")
    ComprasCartoesEntity getByID(int idCompraCartao);

    @Transaction
    @Query("SELECT * FROM COMPRAS_CARTOES WHERE ID_COMPRA_CARTAO == :idCompraCartao")
    List<CompraCartaoWithValoresRelationship> getCompraCartaoWithValores(int idCompraCartao);
}
