package com.example.olxclone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olxclone.R;
import com.example.olxclone.api.CEPService;
import com.example.olxclone.helper.GetFirebase;
import com.example.olxclone.model.Endereco;
import com.example.olxclone.model.Local;
import com.example.olxclone.model.Usuario;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskara.widget.MaskEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormEnderecoActivity extends AppCompatActivity {

    private MaskEditText editCep;
    private EditText editEstado;
    private EditText editCidade;
    private EditText editBairro;

    private ProgressBar progressBar;

    private Usuario usuario;

    private Retrofit retrofit;
    private Local local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_endereco);

        // Inicia componentes
        iniciaComponentes();

        // Recupera Usuário do Firebase
        recuperaUsuario();

        // Ouvinte Cliques
        configCliques();

        // Inicia serviço Retrofit
        iniciaRetrofit();

        // Configura o Cep para busca
        configCep();

    }

    // Inicia serviço Retrofit
    private void iniciaRetrofit() {
        retrofit = new Retrofit
                .Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Configura o Cep para busca
    private void configCep() {
        editCep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String cep = charSequence.toString()
                        .replace("-", "")
                        .replaceAll("_", "");

                if (cep.length() == 8) {

                    // Oculta o teclado do dispotivo
                    ocultaTeclado();

                    // Realiza a chamada da busca
                    // do endereço com base no cep digitado
                    buscarEndereco(cep);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    // Exibe o endereço do cep digitado
    private void configEndereco() {
        if (local != null) {
            editCidade.setText(local.getLocalidade());
            editEstado.setText(local.getUf());
            editBairro.setText(local.getBairro());
        } else {
            editEstado.getText().clear();
            editCidade.getText().clear();
            editBairro.getText().clear();
        }
        progressBar.setVisibility(View.GONE);
    }

    // Realiza a chamada da busca
    // do endereço com base no cep digitado
    private void buscarEndereco(String cep) {
        progressBar.setVisibility(View.VISIBLE);

        CEPService cepService = retrofit.create(CEPService.class);
        Call<Local> call = cepService.recuperarCEP(cep);

        call.enqueue(new Callback<Local>() {
            @Override
            public void onResponse(Call<Local> call, Response<Local> response) {

                if (response.isSuccessful()) {

                    local = response.body();

                    if (local == null) {
                        Toast.makeText(FormEnderecoActivity.this, "Digite um cep válido.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(FormEnderecoActivity.this, "Não foi possível recuperar o endereço.", Toast.LENGTH_SHORT).show();
                }

                // Exibe o endereço do cep digitado
                configEndereco();
            }

            @Override
            public void onFailure(Call<Local> call, Throwable t) {
                Toast.makeText(FormEnderecoActivity.this, "Não foi possível recuperar o endereço.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Recupera Usuário do Firebase
    private void recuperaUsuario() {
        DatabaseReference usuarioRef = GetFirebase.getDatabase()
                .child("usuarios")
                .child(GetFirebase.getIdFirebase());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    usuario = snapshot.getValue(Usuario.class);

                    // Configura as informações nos elementos
                    configDados();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Valida as informações inseridas
    public void validaDados(View view) {
        String cep = editCep.getUnMasked();
        String uf = editEstado.getText().toString();
        String cidade = editCidade.getText().toString();
        String bairro = editBairro.getText().toString();

        if (!cep.trim().isEmpty()) {
            if (!uf.trim().isEmpty()) {
                if(!cidade.trim().isEmpty()){
                    if (!bairro.trim().isEmpty()) {

                        progressBar.setVisibility(View.VISIBLE);

                        // Oculta o teclado do dispositivo
                        ocultaTeclado();

                        if (usuario != null) {

                            Endereco endereco = new Endereco();
                            endereco.setCep(cep);
                            endereco.setUf(uf);
                            endereco.setLocalidade(cidade);
                            endereco.setBairro(bairro);

                            usuario.setEndereco(endereco);
                            usuario.salvar();
                            Snackbar.make(progressBar, "Endereço salvo com sucesso.", Snackbar.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(this, "Não foi possível salvar as informações.", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);

                    } else {
                        editBairro.requestFocus();
                        editBairro.setError("Informe o bairro.");
                    }
                }else {
                    editCidade.requestFocus();
                    editCidade.setError("Informe o município.");
                }
            } else {
                editEstado.requestFocus();
                editEstado.setError("Informe a sigla do estado.");
            }
        } else {
            editCep.requestFocus();
            editCep.setError("Informe o CEP.");
        }

    }

    // Oculta o teclado do dispositivo
    private void ocultaTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editCep.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Configura as informações nos elementos
    private void configDados() {
        if(usuario.getEndereco() != null){
            editCep.setText(usuario.getEndereco().getCep());
            editEstado.setText(usuario.getEndereco().getUf());
            editCidade.setText(usuario.getEndereco().getLocalidade());
            editBairro.setText(usuario.getEndereco().getBairro());
        }

        progressBar.setVisibility(View.GONE);
    }

    // Ouvinte Cliques
    private void configCliques() {
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    // Inicia componentes de tela
    private void iniciaComponentes() {
        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Meu endereço");

        editCep = findViewById(R.id.editCep);
        editEstado = findViewById(R.id.editEstado);
        editCidade = findViewById(R.id.editCidade);
        editBairro = findViewById(R.id.editBairro);
        progressBar = findViewById(R.id.progressBar);
    }

}