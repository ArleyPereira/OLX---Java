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
import com.santalu.maskara.widget.MaskEditText;

public class CriarContaActivity extends AppCompatActivity {

    private EditText editNome;
    private EditText editEmail;
    private MaskEditText editTelefone;
    private EditText editSenha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        // Inicia componentes de tela
        iniciaComponentes();

        // Ouvinte Cliques
        configCliques();

    }

    // Ouvinte Cliques
    private void configCliques() {
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    // Valida as informações
    public void validaDados(View view) {

        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String telefone = editTelefone.getUnMasked();
        String senha = editSenha.getText().toString();

        if (!nome.trim().isEmpty()) {
            if (!email.trim().isEmpty()) {
                if(!telefone.trim().isEmpty()){
                    if (!senha.trim().isEmpty()) {

                        progressBar.setVisibility(View.VISIBLE);

                        // Oculta o teclado do dispositivo
                        ocultaTeclado();

                        Usuario usuario = new Usuario();
                        usuario.setNome(nome);
                        usuario.setEmail(email);
                        usuario.setTelefone(telefone);
                        usuario.setSenha(senha);

                        // Efetua cadastro no Firebase
                        efetuarCadastro(usuario);

                    } else {
                        editSenha.requestFocus();
                        editSenha.setError("Informe a senha.");
                    }
                }else {
                    editTelefone.requestFocus();
                    editTelefone.setError("Informe o telefone.");
                }
            } else {
                editEmail.requestFocus();
                editEmail.setError("Informe o email.");
            }
        } else {
            editNome.requestFocus();
            editNome.setError("Informe o nome.");
        }

    }

    // Efetua cadastro no Firebase
    private void efetuarCadastro(Usuario usuario) {
        GetFirebase.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                usuario.setId(task.getResult().getUser().getUid());
                usuario.salvar();

                finish();
                startActivity(new Intent(this, MainActivity.class));

            } else { // Valida erros
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, GetFirebase.getMsg(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
            }

        });
    }

    // Oculta o teclado do dispositivo
    private void ocultaTeclado(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editNome.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Inicia componentes de tela
    private void iniciaComponentes() {
        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Criar conta");
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editTelefone = findViewById(R.id.editTelefone);
        editSenha = findViewById(R.id.editSenha);
        progressBar = findViewById(R.id.progressBar);
    }

}