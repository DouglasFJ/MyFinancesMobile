package com.douglasfj.myfinances.ui.ledger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.*;
import androidx.lifecycle.ViewModelProvider;
import com.douglasfj.myfinances.R;

/** A separate full-height window, with no bottom navigation. */
public class LedgerActivity extends AppCompatActivity {
    public static Intent intent(Context context,boolean income) {
        return new Intent(context,LedgerActivity.class).putExtra("income",income);
    }
    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        FrameLayout root=new FrameLayout(this); root.setId(R.id.ledger_host); setContentView(root);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        WindowCompat.getInsetsController(getWindow(),root).setAppearanceLightStatusBars(getResources().getBoolean(R.bool.finance_light_system_bars));
        WindowCompat.getInsetsController(getWindow(),root).setAppearanceLightNavigationBars(getResources().getBoolean(R.bool.finance_light_system_bars));
        ViewCompat.setOnApplyWindowInsetsListener(root,(view,insets) -> {
            androidx.core.graphics.Insets bars=insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left,bars.top,bars.right,bars.bottom); return insets;
        });
        if(saved==null) {
            LedgerFragment fragment=new LedgerFragment(); Bundle args=new Bundle();
            args.putBoolean("income",getIntent().getBooleanExtra("income",false)); fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.ledger_host,fragment).commit();
        }
        new ViewModelProvider(this).get(LedgerViewModel.class).changed().observe(this,changed -> {
            if(changed) setResult(RESULT_OK);
        });
        androidx.activity.OnBackPressedCallback waitForSave=new androidx.activity.OnBackPressedCallback(false) {
            @Override public void handleOnBackPressed() { /* Finish only after pending work completes. */ }
        };
        getOnBackPressedDispatcher().addCallback(this,waitForSave);
        new ViewModelProvider(this).get(LedgerViewModel.class).busy().observe(this,waitForSave::setEnabled);
    }
}
