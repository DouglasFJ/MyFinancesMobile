package com.douglasfj.myfinances.models.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ValorWithMesesValoresRelationship {
    @Embedded
    public ValoresEntity valor;

    @Relation(
            parentColumn = "ID_VALOR",
            entityColumn = "ID_VALOR"
    )
    public List<MesesValoresEntity> mesesValores;
}
