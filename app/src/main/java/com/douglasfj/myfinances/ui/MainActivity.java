package com.douglasfj.myfinances.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ImportProgressDialog importProgress;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.getInsetsController(getWindow(),binding.getRoot()).setAppearanceLightStatusBars(getResources().getBoolean(R.bool.finance_light_system_bars));
        WindowCompat.getInsetsController(getWindow(),binding.getRoot()).setAppearanceLightNavigationBars(getResources().getBoolean(R.bool.finance_light_system_bars));
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(),(view,insets) -> {
            androidx.core.graphics.Insets bars=insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(bars.left,bars.top,bars.right,bars.bottom); return insets;
        });
        androidx.navigation.NavController navigation=Navigation.findNavController(this,R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView,navigation);
        android.content.res.ColorStateList defaultIndicator=binding.navView.getItemActiveIndicatorColor();
        navigation.addOnDestinationChangedListener((controller,destination,arguments) -> {
            boolean amber=destination.getId()==R.id.navigation_card || destination.getId()==R.id.navigation_invoice;
            binding.navView.setItemActiveIndicatorColor(amber
                    ? android.content.res.ColorStateList.valueOf(getColor(R.color.amber_200)) : defaultIndicator);
        });
        FinanceViewModel model=new ViewModelProvider(this).get(FinanceViewModel.class);
        model.importing().observe(this,importing -> {
            if (getSupportFragmentManager().isStateSaved()) return;
            androidx.fragment.app.Fragment progress=getSupportFragmentManager().findFragmentByTag("database-import-progress");
            if (progress instanceof ImportProgressDialog) importProgress=(ImportProgressDialog) progress;
            if (importing && importProgress==null) {
                importProgress=new ImportProgressDialog();
                importProgress.show(getSupportFragmentManager(),"database-import-progress");
            } else if (!importing && importProgress!=null) {
                importProgress.dismiss(); importProgress=null;
            }
        });
        model.message().observe(this,event -> {
            String message=event.consume(); if(message!=null) Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        });
    }
}
