package com.douglasfj.myfinances.models.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CartaoWithComprasCartoesRelationship {
    @Embedded
    public CartoesEntity cartao;
    @Relation(
            parentColumn = "ID_CARTAO",
            entityColumn = "ID_CARTAO"
    )
    public List<ComprasCartoesEntity> comprasCartoes;
}
