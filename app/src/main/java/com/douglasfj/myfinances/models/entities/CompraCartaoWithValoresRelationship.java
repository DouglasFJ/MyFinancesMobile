package com.douglasfj.myfinances.models.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CompraCartaoWithValoresRelationship {
    @Embedded public ComprasCartoesEntity compraCartao;
    @Relation(
            parentColumn = "ID_COMPRA_CARTAO",
            entityColumn = "ID_COMPRA_CARTAO"
    )
    public List<ValoresEntity> valores;
}
