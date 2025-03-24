package com.douglasfj.myfinances.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.douglasfj.myfinances.converters.MyFinancesConverters;
import com.douglasfj.myfinances.dao.CartoesDAO;
import com.douglasfj.myfinances.dao.ComprasCartoesDAO;
import com.douglasfj.myfinances.dao.MesesDAO;
import com.douglasfj.myfinances.dao.MesesValoresDAO;
import com.douglasfj.myfinances.dao.ValoresDAO;
import com.douglasfj.myfinances.models.entities.CartoesEntity;
import com.douglasfj.myfinances.models.entities.ComprasCartoesEntity;
import com.douglasfj.myfinances.models.entities.MesesEntity;
import com.douglasfj.myfinances.models.entities.MesesValoresEntity;
import com.douglasfj.myfinances.models.entities.ValoresEntity;

@Database(entities = {CartoesEntity.class, ComprasCartoesEntity.class, MesesEntity.class, MesesValoresEntity.class, ValoresEntity.class}, version = 1)
@TypeConverters({MyFinancesConverters.class})
public abstract class MyFinancesDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "myfinances-database.db";
    public abstract CartoesDAO cartoesDAO();

    public abstract ComprasCartoesDAO comprasCartoesDAO();

    public abstract MesesDAO mesesDAO();

    public abstract MesesValoresDAO mesesValoresDAO();

    public abstract ValoresDAO valoresDAO();

    public static MyFinancesDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context, MyFinancesDatabase.class, DATABASE_NAME).build();
    }
}
