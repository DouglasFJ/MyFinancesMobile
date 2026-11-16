package com.douglasfj.myfinances.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/** Replaces the entire database file; validation only touches a private staged copy. */
public final class DatabaseFileImporter {
    private DatabaseFileImporter() {}

    public static MyFinancesDatabase open(Context context, String name) {
        return Room.databaseBuilder(context.getApplicationContext(), MyFinancesDatabase.class, name)
                .addMigrations(MyFinancesDatabase.MIGRATION_1_2).build();
    }

    public static MyFinancesDatabase replace(Context context, String name,
                                            MyFinancesDatabase current, InputStream source) throws IOException {
        File target = context.getDatabasePath(name);
        File directory = target.getParentFile();
        if (directory == null || (!directory.isDirectory() && !directory.mkdirs()))
            throw new IOException("Diretório do banco indisponível.");
        File staged = File.createTempFile(name + ".import-", ".db", directory);
        File original = originalFile(target);
        MyFinancesDatabase replacement = null;
        boolean swapped = false;
        try {
            try (FileOutputStream output = new FileOutputStream(staged)) {
                byte[] buffer = new byte[8192]; int length;
                while ((length = source.read(buffer)) != -1) output.write(buffer, 0, length);
                output.getFD().sync();
            }
            validate(context, staged);
            // Flush the old WAL before moving its database file out of the way.
            SupportSQLiteDatabase active = current.getOpenHelper().getWritableDatabase();
            try (Cursor checkpoint = active.query("PRAGMA wal_checkpoint(TRUNCATE)")) {
                if (checkpoint.moveToFirst() && checkpoint.getInt(0) != 0)
                    throw new IOException("O banco está ocupado. Tente novamente.");
            }
            current.close();
            deleteSidecars(target);
            Files.move(target.toPath(), original.toPath(), StandardCopyOption.ATOMIC_MOVE);
            swapped = true;
            Files.move(staged.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
            replacement = open(context, name);
            replacement.getOpenHelper().getWritableDatabase();
            // Removing the old file commits the replacement. Until then recovery can undo it.
            Files.delete(original.toPath());
            swapped = false;
            return replacement;
        } catch (Exception error) {
            if (replacement != null) replacement.close();
            if (swapped) {
                try { recoverInterruptedImport(context, name); }
                catch (IOException recoveryError) { error.addSuppressed(recoveryError); }
            }
            if (error instanceof IllegalArgumentException) throw (IllegalArgumentException) error;
            if (error instanceof IOException) throw (IOException) error;
            throw new IOException("Não foi possível substituir o banco.", error);
        } finally {
            try {
                Files.deleteIfExists(staged.toPath());
                deleteSidecars(staged);
            } catch (IOException cleanupError) {
                android.util.Log.w("MyFinances", "Could not remove import staging files", cleanupError);
            }
        }
    }

    /** A crash before commit must restore the old database before Room creates/opens anything. */
    public static void recoverInterruptedImport(Context context, String name) throws IOException {
        File target = context.getDatabasePath(name);
        File original = originalFile(target);
        if (!original.isFile()) return;
        deleteSidecars(target);
        Files.deleteIfExists(target.toPath());
        Files.move(original.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }

    private static File originalFile(File target) { return new File(target.getPath() + ".before-import"); }

    private static void deleteSidecars(File file) throws IOException {
        for (String suffix : new String[]{"-wal", "-shm", "-journal"})
            Files.deleteIfExists(new File(file.getPath() + suffix).toPath());
    }

    private static void validate(Context context, File file) throws IOException {
        byte[] header = new byte[16];
        try (DataInputStream input = new DataInputStream(new FileInputStream(file))) { input.readFully(header); }
        catch (EOFException error) { throw invalid(error); }
        if (!Arrays.equals(header, "SQLite format 3\0".getBytes(StandardCharsets.US_ASCII))) throw invalid(null);
        try {
            try (SQLiteDatabase sqlite = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READWRITE)) {
                int version = sqlite.getVersion();
                if (version < 1 || version > 2)
                    throw new IllegalArgumentException("A versão deste banco não é compatível com o MyFinances. Seus dados atuais não foram alterados.");
                try (Cursor integrity = sqlite.rawQuery("PRAGMA integrity_check", null)) {
                    if (!integrity.moveToFirst() || !"ok".equals(integrity.getString(0)) || integrity.moveToNext()) throw invalid(null);
                }
                // Force Room to validate the real schema rather than trusting a stored identity hash.
                sqlite.execSQL("DROP TABLE IF EXISTS room_master_table");
            }
            MyFinancesDatabase candidate = Room.databaseBuilder(context, MyFinancesDatabase.class, file.getAbsolutePath())
                    .addMigrations(MyFinancesDatabase.MIGRATION_1_2)
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE).build();
            try {
                SupportSQLiteDatabase db = candidate.getOpenHelper().getWritableDatabase();
                rejectRows(db, "SELECT 1 FROM MESES WHERE MES NOT BETWEEN 1 AND 12 OR ANO NOT BETWEEN 1 AND 9999");
                rejectRows(db, "SELECT 1 FROM MESES GROUP BY MES, ANO HAVING COUNT(*) > 1");
                rejectRows(db, "SELECT 1 FROM MESES_VALORES MV LEFT JOIN MESES M ON M.ID_MES=MV.ID_MES " +
                        "LEFT JOIN VALORES V ON V.ID_VALOR=MV.ID_VALOR WHERE M.ID_MES IS NULL OR V.ID_VALOR IS NULL");
                rejectRows(db, "SELECT 1 FROM MESES_VALORES GROUP BY ID_MES, ID_VALOR HAVING COUNT(*) > 1");
                rejectRows(db, "SELECT 1 FROM COMPRAS_CARTOES P LEFT JOIN CARTOES C ON C.ID_CARTAO=P.ID_CARTAO " +
                        "WHERE C.ID_CARTAO IS NULL OR P.NUMERO_PARCELAS NOT BETWEEN 1 AND 120");
                rejectRows(db, "SELECT 1 FROM VALORES V LEFT JOIN COMPRAS_CARTOES P ON P.ID_COMPRA_CARTAO=V.ID_COMPRA_CARTAO " +
                        "WHERE (V.ID_COMPRA_CARTAO IS NOT NULL AND P.ID_COMPRA_CARTAO IS NULL) " +
                        "OR V.VALOR_REAIS < 0 OR V.VALOR_REAIS > 999999999.99");
                // Also exercise enum/date converters before touching the current database.
                candidate.valoresDAO().getAll();
            } finally { candidate.close(); }
        } catch (IllegalArgumentException error) { throw error; }
        catch (RuntimeException error) { throw invalid(error); }
    }

    private static void rejectRows(SupportSQLiteDatabase db, String sql) {
        try (Cursor cursor = db.query(sql)) { if (cursor.moveToFirst()) throw invalid(null); }
    }

    private static IllegalArgumentException invalid(Exception cause) {
        return new IllegalArgumentException("O arquivo está inválido, corrompido ou não é um backup compatível do MyFinances. Seus dados atuais não foram alterados.", cause);
    }
}
