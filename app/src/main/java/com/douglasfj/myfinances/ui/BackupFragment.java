package com.douglasfj.myfinances.ui;

import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupFragment extends Fragment {
    private Button button;
    private Button importButton;
    private final ActivityResultLauncher<String[]> importFile = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) return;
                ImportConfirmationDialog.forFile(uri, fileName(uri)).show(getParentFragmentManager(), "confirm-database-import");
            });
    private final ActivityResultLauncher<String> export = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("application/vnd.sqlite3"),uri -> {
                if(uri!=null) new ViewModelProvider(requireActivity()).get(FinanceViewModel.class).export(uri);
            });
    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle saved) {
        ScrollView scroll=new ScrollView(requireContext()); LinearLayout content=FinanceUi.page(requireContext(),scroll);
        FinanceUi.text(content,"Backup dos seus dados",26);
        LinearLayout card=FinanceUi.card(content);
        FinanceUi.text(card,"Suas finanças funcionam offline",21);
        FinanceUi.text(card,"Receitas, despesas, impostos, cartões e parcelas ficam salvos neste aparelho, mesmo sem internet.",16);
        FinanceUi.text(card,"Exporte uma cópia completa do banco SQLite para guardar em uma pasta ou em um serviço de nuvem disponível no seletor de arquivos do Android.",16);
        button=FinanceUi.button(content,"Exportar banco SQLite",v -> export.launch("MyFinances-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".db"));
        FinanceUi.text(content,"O backup é uma cópia dos dados no momento da exportação. Exporte novamente após fazer alterações. A sincronização com a nuvem não é automática.",14);
        LinearLayout restore = FinanceUi.card(content);
        FinanceUi.text(restore,"Importar um backup",21);
        FinanceUi.text(restore,"Selecione um arquivo .db exportado pelo MyFinances. O arquivo será validado antes de substituir o banco atual.",16);
        TextView warning=FinanceUi.text(restore,"Atenção: a importação apaga todos os dados atuais e os substitui pelos dados do arquivo. Exporte um backup antes de continuar.",16);
        warning.setTextColor(android.graphics.Color.rgb(185,28,28));
        // .db files are often reported as generic binary files by document providers.
        importButton=FinanceUi.button(restore,"Importar banco SQLite (.db)",v -> importFile.launch(new String[]{"*/*"}));
        return scroll;
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        new ViewModelProvider(requireActivity()).get(FinanceViewModel.class).busy().observe(getViewLifecycleOwner(),busy -> {
            button.setEnabled(!busy); importButton.setEnabled(!busy);
        });
    }
    private String fileName(Uri uri) {
        try (Cursor cursor=requireContext().getContentResolver().query(uri,new String[]{OpenableColumns.DISPLAY_NAME},null,null,null)) {
            if (cursor!=null && cursor.moveToFirst()) {
                String name=cursor.getString(0); if (name!=null) return name;
            }
        } catch (RuntimeException ignored) { /* The content is validated independently of its display name. */ }
        return "Arquivo selecionado";
    }
    @Override public void onDestroyView() { super.onDestroyView(); button=null; importButton=null; }
}
