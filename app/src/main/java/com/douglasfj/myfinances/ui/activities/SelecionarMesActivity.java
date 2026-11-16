package com.douglasfj.myfinances.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.douglasfj.myfinances.databinding.ActivitySelecionarMesBinding;

public class SelecionarMesActivity extends AppCompatActivity {

    ActivitySelecionarMesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelecionarMesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}
