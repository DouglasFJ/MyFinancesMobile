package com.douglasfj.myfinances.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ImportProgressDialog extends DialogFragment {
    @Override public void onCreate(Bundle saved) { super.onCreate(saved); setCancelable(false); }

    @NonNull @Override public Dialog onCreateDialog(Bundle saved) {
        ProgressBar progress = new ProgressBar(requireContext());
        int padding = FinanceUi.dp(requireContext(), 24);
        progress.setPadding(padding, padding, padding, padding);
        return new AlertDialog.Builder(requireContext()).setTitle("Importando banco de dados")
                .setMessage("Aguarde a validação e a substituição dos dados.")
                .setView(progress).create();
    }
}
