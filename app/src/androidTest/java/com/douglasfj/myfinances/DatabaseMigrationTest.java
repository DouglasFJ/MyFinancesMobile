package com.douglasfj.myfinances;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseMigrationTest {
    @Rule public MigrationTestHelper helper=new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            MyFinancesDatabase.class.getCanonicalName(),new FrameworkSQLiteOpenHelperFactory());
    @Test public void preservesExistingValuesAndBackfillsRecurringStart() throws Exception {
        SupportSQLiteDatabase old=helper.createDatabase("migration-test",1);
        old.execSQL("INSERT INTO MESES(ID_MES,MES,ANO) VALUES(1,11,2026)");
        old.execSQL("INSERT INTO VALORES(ID_VALOR,NOME_VALOR,VALOR_REAIS,INDICADOR_VALOR_FIXO,INDICADOR_TIPO_VALOR,TIPO_DESPESA) VALUES(1,'Aluguel',1000,1,0,'ESSENCIAL')");
        old.execSQL("INSERT INTO MESES_VALORES(ID_MES,ID_VALOR,INDICADOR_DESPESA_PAGA) VALUES(1,1,1)");
        old.close();
        SupportSQLiteDatabase migrated=helper.runMigrationsAndValidate("migration-test",2,true,MyFinancesDatabase.MIGRATION_1_2);
        try(android.database.Cursor cursor=migrated.query("SELECT NOME_VALOR,VALOR_REAIS,IMPOSTO,MES_INICIO_FIXO FROM VALORES")) {
            assertTrue(cursor.moveToFirst()); assertEquals("Aluguel",cursor.getString(0)); assertEquals(1000,cursor.getDouble(1),0.001);
            assertEquals(0,cursor.getInt(2)); assertEquals(2026*12+10,cursor.getInt(3));
        }
        try(android.database.Cursor cursor=migrated.query("SELECT INDICADOR_DESPESA_PAGA FROM MESES_VALORES")) {
            assertTrue(cursor.moveToFirst()); assertEquals(1,cursor.getInt(0));
        }
        migrated.close();
    }
}
