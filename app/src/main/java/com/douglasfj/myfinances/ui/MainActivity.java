package com.douglasfj.myfinances.ui;

import android.os.Bundle;

import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.dao.MesesDAO;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.models.entities.MesesEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.douglasfj.myfinances.databinding.ActivityMainBinding;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setNavigation();
        setMonthContext();
    }

    private void setMonthContext() {
        MyFinancesDatabase database = MyFinancesDatabase.getDatabase(getBaseContext());
        MesesDAO mesesDAO = database.mesesDAO();
        MesesEntity mesSelecionadoValue = GlobalState.MES_SELECIONADO.getValue();
        Disposable subscribe = mesesDAO.getByMesAno(mesSelecionadoValue.mes, mesSelecionadoValue.ano)
                .subscribeOn(Schedulers.io())
                .subscribe(GlobalState.MES_SELECIONADO::onNext, throwable -> {
                    Disposable subscribe1 = mesesDAO.insertAll(mesSelecionadoValue)
                            .subscribeOn(Schedulers.io())
                            .subscribe(() -> {
                                Disposable subscribe2 = mesesDAO.getByMesAno(mesSelecionadoValue.mes, mesSelecionadoValue.ano)
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(GlobalState.MES_SELECIONADO::onNext);
                                compositeDisposable.add(subscribe2);
                            });
                    compositeDisposable.add(subscribe1);
                });
        compositeDisposable.add(subscribe);
    }

    private void setNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_month, R.id.navigation_card)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}