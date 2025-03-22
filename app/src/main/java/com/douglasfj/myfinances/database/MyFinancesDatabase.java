package com.douglasfj.myfinances.database;

import androidx.room.Database;

import com.douglasfj.myfinances.dao.CartoesDAO;
import com.douglasfj.myfinances.dao.ComprasCartoesDAO;
import com.douglasfj.myfinances.dao.MesesDAO;
import com.douglasfj.myfinances.models.entities.CartoesEntity;
import com.douglasfj.myfinances.models.entities.ComprasCartoesEntity;
import com.douglasfj.myfinances.models.entities.MesesEntity;
import com.douglasfj.myfinances.models.entities.MesesValoresEntity;
import com.douglasfj.myfinances.models.entities.ValoresEntity;

@Database(entities = {CartoesEntity.class, ComprasCartoesEntity.class, MesesEntity.class, MesesValoresEntity.class, ValoresEntity.class}, version = 1)
public abstract class MyFinancesDatabase {
    public abstract CartoesDAO cartoesDAO();

    public abstract ComprasCartoesDAO comprasCartoesDAO();

    public abstract MesesDAO mesesDAO();

}
