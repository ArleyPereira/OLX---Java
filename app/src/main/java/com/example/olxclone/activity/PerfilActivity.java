package com.example.olxclone.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olxclone.R;
import com.example.olxclone.helper.GetFirebase;
import com.example.olxclone.model.Usuario;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.santalu.maskara.widget.MaskEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    private static final int SELECAO_GALERIA = 200;

    private Usuario usuario;

    private String caminhoImagem;

    private ImageView imagemPerfil;
    private EditText editNome;
    private EditText editEmail;
    private MaskEditText editTelefone;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicia componentes
        iniciaComponentes();

        // Recupera informações do perfil do Firebase
        recuperaDados();

        // Ouvinte Cliques
        configCliqes();

    }

    // Ouvinte Cliques
    private void configCliqes() {
        findViewById(R.id.ibVoltar).setOnClickListener(view -> finish());
        imagemPerfil.setOnClickListener(view -> verificaPermissaoGaleria());
    }

    // Solicita Permissão Galeria
    private void verificaPermissaoGaleria() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(PerfilActivity.this, "Permissão Negada", Toast.LENGTH_SHORT).show();
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("Permissões")
                .setDeniedMessage("Se você não aceitar a permissão não poderá acessar a Galeria do dispositivo, deseja ativar a permissão agora ?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

    }

    // Abre a galeria do Aparelho
    public void abrirGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECAO_GALERIA);
    }

    // Recupera informações do perfil do Firebase
    private void recuperaDados() {
        DatabaseReference perfilRef = GetFirebase.getDatabase()
                .child("usuarios")
                .child(GetFirebase.getIdFirebase());
        perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    // Configura as informações nos elementos
    private void configDados() {
        if (usuario.getUrlImagem() != null){
            Picasso.get().load(usuario.getUrlImagem())
                    .placeholder(R.drawable.loading)
                    .into(imagemPerfil);
        }else {
            imagemPerfil.setImageResource(R.drawable.ic_user_cinza);
        }


        editNome.setText(usuario.getNome());
        editEmail.setText(usuario.getEmail());
        editTelefone.setText(usuario.getTelefone());

        progressBar.setVisibility(View.GONE);
    }

    // Inicia componentes de tela
    private void iniciaComponentes() {
        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Perfil");

        imagemPerfil = findViewById(R.id.imagemPerfil);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editTelefone = findViewById(R.id.editTelefone);
        progressBar = findViewById(R.id.progressBar);
    }

    // Valida as informações
    public void validaDados(View view) {
        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String telefone = editTelefone.getText().toString();

        if (!nome.trim().isEmpty()) {
            if (!email.trim().isEmpty()) {
                if (!telefone.trim().isEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);

                    // Oculta o teclado do dispositivo
                    ocultaTeclado();

                    if (usuario != null) {
                        usuario.setNome(nome);
                        usuario.setEmail(email);
                        usuario.setTelefone(telefone);

                        if (caminhoImagem != null) {
                            salvarImagemPerfil();
                        } else {
                            usuario.salvar();
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(progressBar, "Perfil salvo com sucesso.", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Não foi possível salvar as informações.", Toast.LENGTH_SHORT).show();
                    }
                } else {
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

    // Salvar Imagem perfil Firebase
    private void salvarImagemPerfil() {
        StorageReference perfil = GetFirebase.getStorage()
                .child("imagens")
                .child("perfil")
                .child(GetFirebase.getIdFirebase() + ".jpeg");

        UploadTask uploadTask = perfil.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> perfil.getDownloadUrl().addOnCompleteListener(task -> {

            usuario.setUrlImagem(String.valueOf(task.getResult()));
            usuario.salvar();

            progressBar.setVisibility(View.GONE);
            Snackbar.make(progressBar, "Sucesso", Snackbar.LENGTH_SHORT).show();

        })).addOnFailureListener(e -> {
            Toast.makeText(this, "Falha no Upload.", Toast.LENGTH_SHORT).show();
        });

    }

    // Oculta o teclado do dispositivo
    private void ocultaTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editNome.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            Bitmap imagemRecuperada;

            try {

                imagemRecuperada = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);

                if (requestCode == SELECAO_GALERIA) {
                    imagemPerfil.setImageBitmap(imagemRecuperada);
                }

                caminhoImagem = imagemSelecionada.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}