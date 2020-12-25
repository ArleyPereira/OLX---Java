package com.example.olxclone.activity.autenticacao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.olxclone.R;
import com.example.olxclone.activity.MainActivity;
import com.example.olxclone.helper.GetFirebase;
import com.example.olxclone.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText editSenha;
    private EditText editEmail;
    private ProgressBar progressBar;

    private TextView textCriarConta;
    private TextView textRecuperarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicia componentes de tela
        iniciaComponentes();

        // Ouvinte Cliques
        configCliques();

    }

    // Valida as informações
    public void validaDados(View view){

        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        if(!email.trim().isEmpty()){
            if(!senha.trim().isEmpty()){

                progressBar.setVisibility(View.VISIBLE);

                // Oculta o teclado do dispositivo
                ocultaTeclado();

                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(senha);

                // Efetua login no Firebase
                efetuarLogin(usuario);

            }else {
                editSenha.requestFocus();
                editSenha.setError("Informe a senha.");
            }
        }else {
            editEmail.requestFocus();
            editEmail.setError("Informe o email.");
        }

    }

    // Oculta o teclado do dispositivo
    private void ocultaTeclado(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editEmail.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Efetua login no Firebase
    private void efetuarLogin(Usuario usuario){
        GetFirebase.getAuth().signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                finish();
                startActivity(new Intent(this, MainActivity.class));

            }else { // Valida erros
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, GetFirebase.getMsg(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
            }

        });
    }

    // Ouvinte Cliques
    private void configCliques(){
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
        textCriarConta.setOnClickListener(v -> startActivity(new Intent(this, CriarContaActivity.class)));
        textRecuperarSenha.setOnClickListener(v -> startActivity(new Intent(this, RecuperarSenhaActivity.class)));
    }

    // Inicia componentes de tela
    private void iniciaComponentes(){
        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Acesse sua conta");
        editSenha = findViewById(R.id.editSenha);
        editEmail = findViewById(R.id.editEmail);
        progressBar = findViewById(R.id.progressBar);
        textCriarConta = findViewById(R.id.textCriarConta);
        textRecuperarSenha = findViewById(R.id.textRecuperarSenha);
    }

}