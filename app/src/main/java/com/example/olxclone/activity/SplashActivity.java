package com.example.olxclone.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.olxclone.R;
import com.example.olxclone.Util.SPFiltro;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Leva o Usuário para tela principal
        new Handler(Looper.getMainLooper()).postDelayed(this::homeApp, 3000);

        // Limpa todos os filtros
        SPFiltro.limpaFiltros(this);

    }

    // Leva o Usuário para tela principal
    private void homeApp(){
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

}