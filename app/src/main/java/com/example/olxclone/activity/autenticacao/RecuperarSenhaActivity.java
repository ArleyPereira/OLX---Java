package com.example.olxclone.activity.autenticacao;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.olxclone.R;

public class RecuperarSenhaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        // Inicia componentes de tela
        iniciaComponentes();

    }

    // Inicia componentes de tela
    private void iniciaComponentes(){
        findViewById(R.id.ibVoltar).setOnClickListener(view -> finish());
        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Recuperar conta");
    }

}