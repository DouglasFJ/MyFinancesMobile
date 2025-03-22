package com.douglasfj.myfinances.models.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class MesWithMesesValoresRelationship {
    @Embedded
    public MesesEntity mes;

    @Relation(
            parentColumn = "ID_MES",
            entityColumn = "ID_MES"
    )
    public List<MesesValoresEntity> mesesValores;
}
