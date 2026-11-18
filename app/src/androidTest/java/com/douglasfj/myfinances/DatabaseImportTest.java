package com.douglasfj.myfinances;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.database.*;
import com.douglasfj.myfinances.data.*;
import org.junit.*;
import org.junit.runner.RunWith;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.YearMonth;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseImportTest {
    private static final String TARGET="restore-test-target.db", SOURCE="restore-test-source.db";
    private Context context;
    private MyFinancesDatabase target, source;
    private final YearMonth month=YearMonth.of(2026,11);
    @Rule public MigrationTestHelper helper=new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            MyFinancesDatabase.class.getCanonicalName(),new FrameworkSQLiteOpenHelperFactory());

    @Before public void setup() throws Exception {
        context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(TARGET); context.deleteDatabase(SOURCE);
        Files.deleteIfExists(new File(context.getDatabasePath(TARGET)+".before-import").toPath());
        target=DatabaseFileImporter.open(context,TARGET);
        source=DatabaseFileImporter.open(context,SOURCE);
        new RoomFinanceRepository(target).saveEntry(null,month,"Dados anteriores",700000,true,false,false,false);
    }
    @After public void cleanup() {
        if(target!=null) target.close(); if(source!=null) source.close();
        context.deleteDatabase(TARGET); context.deleteDatabase(SOURCE);
    }
    private void replace(File file) throws Exception {
        try(InputStream input=new FileInputStream(file)) {
            target=DatabaseFileImporter.replace(context,TARGET,target,input);
        }
    }
    private void assertOldData() {
        assertEquals(1,target.valoresDAO().getAll().size());
        assertEquals("Dados anteriores",target.valoresDAO().getAll().get(0).nomeValor);
    }

    @Test public void replacesEntireDatabaseAndLeavesSourceUnchanged() throws Exception {
        RoomFinanceRepository repository=new RoomFinanceRepository(source);
        repository.saveEntry(null,month,"Receita importada",123456,true,false,true,false);
        repository.saveCard(null,"Cartão importado");
        long card=repository.load(month).cards.get(0).idCartao;
        repository.savePurchase(null,card,"Compra importada",10000,3,month);
        File file=repository.backup(context.getCacheDir());
        byte[] original=Files.readAllBytes(file.toPath());
        try {
            replace(file);
            assertArrayEquals(original,Files.readAllBytes(file.toPath()));
            assertEquals(4,target.valoresDAO().getAll().size());
            assertFalse(target.valoresDAO().getAll().stream().anyMatch(value -> "Dados anteriores".equals(value.nomeValor)));
            assertEquals("Cartão importado",target.financeiroDAO().cards().get(0).nomeCartao);
            assertEquals(3334,new RoomFinanceRepository(target).load(month).invoice(card).totalCents);
            assertFalse(new File(context.getDatabasePath(TARGET)+".before-import").exists());
            target.close(); target=DatabaseFileImporter.open(context,TARGET);
            assertEquals(4,target.valoresDAO().getAll().size());
            new RoomFinanceRepository(target).saveEntry(null,month,"Após importação",100,true,false,false,false);
            assertEquals(5,target.valoresDAO().getAll().size());
        } finally { file.delete(); }
    }

    @Test public void invalidOrTruncatedFilesPreserveCurrentDataAndConnection() throws Exception {
        for(byte[] bytes:new byte[][]{new byte[0],"Não é SQLite".getBytes(java.nio.charset.StandardCharsets.UTF_8),"SQLite format 3\0".getBytes(java.nio.charset.StandardCharsets.US_ASCII)}) {
            try(InputStream input=new ByteArrayInputStream(bytes)) {
                assertThrows(IllegalArgumentException.class,() -> DatabaseFileImporter.replace(context,TARGET,target,input));
            }
            assertOldData();
        }
    }

    @Test public void rejectsWrongSchemaEvenWithOriginalRoomIdentity() throws Exception {
        File file=new RoomFinanceRepository(source).backup(context.getCacheDir());
        try {
            try(SQLiteDatabase edited=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE)) {
                edited.execSQL("ALTER TABLE VALORES ADD COLUMN COLUNA_INCOMPATIVEL TEXT");
            }
            assertThrows(IllegalArgumentException.class,() -> replace(file));
            assertOldData();
        } finally { file.delete(); }
    }

    @Test public void rejectsFutureVersionAndBrokenReferences() throws Exception {
        File file=new RoomFinanceRepository(source).backup(context.getCacheDir());
        try {
            try(SQLiteDatabase edited=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE)) { edited.setVersion(99); }
            assertThrows(IllegalArgumentException.class,() -> replace(file)); assertOldData();
            try(SQLiteDatabase edited=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE)) {
                edited.setVersion(3); edited.execSQL("INSERT INTO MESES_VALORES(ID_MES,ID_VALOR) VALUES(999,999)");
            }
            assertThrows(IllegalArgumentException.class,() -> replace(file)); assertOldData();
        } finally { file.delete(); }
    }

    @Test public void interruptedReadDoesNotTouchCurrentDatabase() throws Exception {
        InputStream failing=new InputStream() {
            @Override public int read() throws IOException { throw new IOException("Leitura interrompida"); }
        };
        assertThrows(IOException.class,() -> DatabaseFileImporter.replace(context,TARGET,target,failing));
        assertOldData();
    }

    @Test public void migratesVersionOneBackupBeforeReplacingCurrentData() throws Exception {
        String legacyName="restore-test-legacy.db";
        SupportSQLiteDatabase legacy=helper.createDatabase(legacyName,1);
        legacy.execSQL("INSERT INTO MESES(ID_MES,MES,ANO) VALUES(1,11,2026)");
        legacy.execSQL("INSERT INTO VALORES(ID_VALOR,NOME_VALOR,VALOR_REAIS,INDICADOR_VALOR_FIXO,INDICADOR_TIPO_VALOR) VALUES(1,'Legado',1000,1,1)");
        legacy.execSQL("INSERT INTO MESES_VALORES(ID_MES,ID_VALOR) VALUES(1,1)"); legacy.close();
        File file=new File(context.getCacheDir(),"restore-test-legacy-copy.db");
        try {
            Files.copy(context.getDatabasePath(legacyName).toPath(),file.toPath(),StandardCopyOption.REPLACE_EXISTING);
            replace(file);
            assertEquals("Legado",target.valoresDAO().getAll().get(0).nomeValor);
            assertEquals(Integer.valueOf(2026*12+10),target.valoresDAO().getAll().get(0).mesInicioFixo);
            assertEquals(3,target.getOpenHelper().getReadableDatabase().getVersion());
        } finally { file.delete(); context.deleteDatabase(legacyName); }
    }

    @Test public void restoresOriginalAfterInterruptedFileSwap() throws Exception {
        try(Cursor checkpoint=target.getOpenHelper().getWritableDatabase().query("PRAGMA wal_checkpoint(TRUNCATE)")) { assertTrue(checkpoint.moveToFirst()); }
        target.close();
        File database=context.getDatabasePath(TARGET);
        Files.move(database.toPath(),new File(database+".before-import").toPath(),StandardCopyOption.ATOMIC_MOVE);
        Files.write(database.toPath(),new byte[]{1,2,3});
        DatabaseFileImporter.recoverInterruptedImport(context,TARGET);
        target=DatabaseFileImporter.open(context,TARGET);
        assertOldData();
        assertFalse(new File(database+".before-import").exists());
    }
}
