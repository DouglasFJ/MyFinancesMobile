package com.douglasfj.myfinances.ui;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.douglasfj.myfinances.R;

/** Keeps the selected file and the destructive-action warning through rotation. */
public class ImportConfirmationDialog extends DialogFragment {
    public static ImportConfirmationDialog forFile(Uri uri, String name) {
        ImportConfirmationDialog dialog = new ImportConfirmationDialog();
        Bundle arguments = new Bundle();
        arguments.putString("uri", uri.toString()); arguments.putString("name", name);
        dialog.setArguments(arguments);
        return dialog;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle saved) {
        Bundle arguments = requireArguments();
        return new AlertDialog.Builder(requireContext()).setTitle("Substituir todos os dados?")
                .setMessage("Arquivo: " + arguments.getString("name") + "\n\n" +
                        "Todos os dados salvos atualmente neste aplicativo serão apagados e substituídos pelos dados do arquivo selecionado. " +
                        "Isso inclui receitas, despesas, cartões e parcelas.\n\n" +
                        "Esta ação não pode ser desfeita. Exporte um backup antes de continuar.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Apagar dados e importar", (dialog, which) ->
                        new ViewModelProvider(requireActivity()).get(FinanceViewModel.class)
                                .importDatabase(Uri.parse(arguments.getString("uri"))))
                .create();
    }

    @Override public void onStart() {
        super.onStart();
        ((AlertDialog) requireDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(requireContext().getColor(R.color.dialog_error));
    }
}
