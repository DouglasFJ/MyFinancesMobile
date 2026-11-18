package com.douglasfj.myfinances.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;
import com.douglasfj.myfinances.dao.FinanceiroDAO;
import java.io.IOException;
import java.io.InputStream;

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

@Database(entities = {CartoesEntity.class, ComprasCartoesEntity.class, MesesEntity.class, MesesValoresEntity.class, ValoresEntity.class}, version = 3)
@TypeConverters({MyFinancesConverters.class})
public abstract class MyFinancesDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "myfinances-database.db";
    private static volatile MyFinancesDatabase instance;
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE VALORES ADD COLUMN IMPOSTO INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE VALORES ADD COLUMN MES_INICIO_FIXO INTEGER");
            db.execSQL("ALTER TABLE VALORES ADD COLUMN MES_FIM_FIXO INTEGER");
            db.execSQL("UPDATE VALORES SET MES_INICIO_FIXO = (SELECT MIN(M.ANO * 12 + M.MES - 1) " +
                    "FROM MESES M JOIN MESES_VALORES MV ON MV.ID_MES = M.ID_MES " +
                    "WHERE MV.ID_VALOR = VALORES.ID_VALOR) WHERE INDICADOR_VALOR_FIXO = 1 AND ID_COMPRA_CARTAO IS NULL");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `VALORES_NEW` (`ID_VALOR` INTEGER PRIMARY KEY AUTOINCREMENT, `NOME_VALOR` TEXT, `VALOR_REAIS` INTEGER NOT NULL, `DATA_INSERCAO_VALOR` INTEGER, `INDICADOR_VALOR_FIXO` INTEGER, `INDICADOR_TIPO_VALOR` INTEGER, `TIPO_DESPESA` TEXT, `ID_COMPRA_CARTAO` INTEGER, `IMPOSTO` INTEGER NOT NULL DEFAULT 0, `MES_INICIO_FIXO` INTEGER, `MES_FIM_FIXO` INTEGER)");
            db.execSQL("INSERT INTO VALORES_NEW (`ID_VALOR`,`NOME_VALOR`,`VALOR_REAIS`,`DATA_INSERCAO_VALOR`,`INDICADOR_VALOR_FIXO`,`INDICADOR_TIPO_VALOR`,`TIPO_DESPESA`,`ID_COMPRA_CARTAO`,`IMPOSTO`,`MES_INICIO_FIXO`,`MES_FIM_FIXO`) SELECT `ID_VALOR`,`NOME_VALOR`,CAST(ROUND(VALOR_REAIS * 100) AS INTEGER),`DATA_INSERCAO_VALOR`,`INDICADOR_VALOR_FIXO`,`INDICADOR_TIPO_VALOR`,`TIPO_DESPESA`,`ID_COMPRA_CARTAO`,`IMPOSTO`,`MES_INICIO_FIXO`,`MES_FIM_FIXO` FROM VALORES");
            db.execSQL("DROP TABLE VALORES");
            db.execSQL("ALTER TABLE VALORES_NEW RENAME TO VALORES");
        }
    };

    public abstract FinanceiroDAO financeiroDAO();
    public abstract CartoesDAO cartoesDAO();

    public abstract ComprasCartoesDAO comprasCartoesDAO();

    public abstract MesesDAO mesesDAO();

    public abstract MesesValoresDAO mesesValoresDAO();

    public abstract ValoresDAO valoresDAO();

    public static synchronized MyFinancesDatabase getDatabase(Context context) {
        if (instance == null) {
            try { DatabaseFileImporter.recoverInterruptedImport(context, DATABASE_NAME); }
            catch (IOException error) { throw new IllegalStateException("Não foi possível recuperar o banco anterior.", error); }
            instance = DatabaseFileImporter.open(context, DATABASE_NAME);
        }
        return instance;
    }

    public static MyFinancesDatabase getMainThreadDatabase(Context context) {
        return getDatabase(context);
    }

    public static synchronized MyFinancesDatabase importDatabase(Context context, InputStream source) throws IOException {
        MyFinancesDatabase current = getDatabase(context);
        try {
            instance = DatabaseFileImporter.replace(context, DATABASE_NAME, current, source);
            return instance;
        } catch (IOException | RuntimeException error) {
            // replace may have closed the old Room instance while restoring its file.
            current.close();
            instance = null;
            DatabaseFileImporter.recoverInterruptedImport(context, DATABASE_NAME);
            instance = DatabaseFileImporter.open(context, DATABASE_NAME);
            throw error;
        }
    }
}
