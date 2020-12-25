package com.example.olxclone.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxclone.R;
import com.example.olxclone.api.CEPService;
import com.example.olxclone.helper.GetFirebase;
import com.example.olxclone.model.Anuncio;
import com.example.olxclone.model.Categoria;
import com.example.olxclone.model.Imagem;
import com.example.olxclone.model.Local;
import com.example.olxclone.model.Usuario;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FormAnuncioActivity extends AppCompatActivity {

    private static final int REQUEST_CATEGORIA = 10;

    private boolean novoAnuncio = true;

    private TextView textToolbar;

    private ImageView imagem0;
    private ImageView imagem1;
    private ImageView imagem2;

    private EditText editNome;
    private CurrencyEditText editPreco;
    private EditText editDescricao;
    private EditText editCep;
    private Button btnCategoria;
    private ProgressBar progressBar;

    private TextView textLocal;

    private final List<Imagem> imagemList = new ArrayList<>();

    private Anuncio anuncio;
    private Usuario usuario;
    private String categoriaSelecionada;
    private Local local;

    private Retrofit retrofit;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_anuncio);

        // Inicia componentes de tela
        iniciaComponentes();

        // Inicia serviço Retrofit
        iniciaRetrofit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            anuncio = (Anuncio) bundle.getSerializable("anuncioSelecionado");

            if (anuncio != null) {

                // Configura as informações nos elementos
                configDados();
            }
        }

        // Recupera Usuário do Firebase
        recuperaUsuario();

        // Ouvinte Cliques
        configCliques();

        // Configura o Cep para busca
        configCep();

    }

    // Ouvinte Cliques
    private void configCliques() {
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());

        imagem0.setOnClickListener(v -> showDialogImagem(0));
        imagem1.setOnClickListener(v -> showDialogImagem(1));
        imagem2.setOnClickListener(v -> showDialogImagem(2));

        btnCategoria.setOnClickListener(view -> {
            Intent intent = new Intent(this, CategoriasActivity.class);
            intent.putExtra("todasCategorias", false);
            startActivityForResult(intent, REQUEST_CATEGORIA);
        });
    }

    // Exibe dialog ( Camera / Galeria )
    private void showDialogImagem(int requestCode) {
        View modalbottomsheet = getLayoutInflater().inflate(R.layout.bottom_sheet_form_anuncio, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        dialog.setContentView(modalbottomsheet);
        dialog.show();

        modalbottomsheet.findViewById(R.id.btnTirarFoto).setOnClickListener(view -> {
            dialog.dismiss();
            verificaPermissaoCamera(requestCode);
        });

        modalbottomsheet.findViewById(R.id.btnGaleria).setOnClickListener(view -> {
            dialog.dismiss();
            verificaPermissaoGaleria(requestCode);
        });

        modalbottomsheet.findViewById(R.id.btnCancelar).setOnClickListener(view -> dialog.dismiss());
    }

    // Abre a galeria do Aparelho
    public void escolherImagemGaleria(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    // Valida as informações inseridas
    public void validaDados(View view) {
        String nome = editNome.getText().toString();
        double preco = (double) editPreco.getRawValue() / 100;
        String descricao = editDescricao.getText().toString();

        if (!nome.trim().isEmpty()) {
            if (preco > 0) {
                if (!categoriaSelecionada.isEmpty()) {
                    if (!descricao.trim().isEmpty()) {

                        progressBar.setVisibility(View.VISIBLE);

                        // Oculta o teclado do dispotivo
                        ocultaTeclado();

                        if (anuncio == null) anuncio = new Anuncio();

                        if (local.getLocalidade() != null) {
                            anuncio.setIdUsuario(GetFirebase.getIdFirebase());
                            anuncio.setNome(nome);
                            anuncio.setDescricao(descricao);
                            anuncio.setPreco(preco);
                            anuncio.setCategoria(categoriaSelecionada);
                            anuncio.setLocal(local);

                            if (novoAnuncio) { // Novo Produto

                                if (imagemList.size() == 3) {
                                    for (int i = 0; i < imagemList.size(); i++) {
                                        salvaImagemFirebase(imagemList.get(i), i);
                                    }
                                } else {
                                    Toast.makeText(this, "Selecione 3 imagens para o anúncio.", Toast.LENGTH_SHORT).show();
                                }

                            } else { // Edita produto

                                if (imagemList.size() > 0) {

                                    for (int i = 0; i < imagemList.size(); i++) {
                                        salvaImagemFirebase(imagemList.get(i), i);
                                    }

                                } else { // Não teve edições de imagens

                                    // Salva o Anúncio no Firebase
                                    salvarAnuncio();

                                }

                            }

                        } else {
                            Toast.makeText(this, "Informe o CEP para cadastrar o anúncio.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        editDescricao.requestFocus();
                        editDescricao.setError("Informe a descrição do anúncio.");
                    }
                } else {
                    Toast.makeText(this, "Selecione a categoria.", Toast.LENGTH_SHORT).show();
                }
            } else {
                editPreco.requestFocus();
                editPreco.setError("Informe o preço do anúncio.");
            }
        } else {
            editNome.requestFocus();
            editNome.setError("Informe o nome do anúncio.");
        }

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

                } else {

                    local = null;

                    // Exibe o endereço do cep digitado
                    configEndereco();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                        Toast.makeText(FormAnuncioActivity.this, "Digite um cep válido.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(FormAnuncioActivity.this, "Não foi possível recuperar o endereço.", Toast.LENGTH_SHORT).show();
                }

                // Exibe o endereço do cep digitado
                configEndereco();
            }

            @Override
            public void onFailure(Call<Local> call, Throwable t) {
                Toast.makeText(FormAnuncioActivity.this, "Não foi possível recuperar o endereço.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Exibe o endereço do cep digitado
    private void configEndereco() {
        if (local != null) {
            if (local.getLocalidade() != null) {
                StringBuilder end = new StringBuilder();
                end.append(local.getBairro())
                        .append(", ")
                        .append(local.getLocalidade())
                        .append(", ")
                        .append(local.getUf().toUpperCase());
                textLocal.setText(end);
            } else {
                textLocal.setText("");
            }
        } else {
            textLocal.setText("");
        }
        progressBar.setVisibility(View.GONE);
    }

    // Inicia serviço Retrofit
    private void iniciaRetrofit() {
        retrofit = new Retrofit
                .Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Salva a Imagem no Firebase Storage e recupera a URL
    private void salvaImagemFirebase(Imagem imagem, int index) {
        StorageReference StorageReference = GetFirebase.getStorage()
                .child("imagens")
                .child("anuncios")
                .child(anuncio.getId())
                .child("imagem" + imagem.getCaminhoImagem() + ".jpeg");

        UploadTask uploadTask = StorageReference.putFile(Uri.parse(imagem.getCaminhoImagem()));
        uploadTask.addOnSuccessListener(taskSnapshot -> StorageReference.getDownloadUrl().addOnCompleteListener(task -> {

            if (novoAnuncio) {
                anuncio.getUrlFotos().add(task.getResult().toString());
            } else {
                anuncio.getUrlFotos().set(imagem.getIndex(), task.getResult().toString());
            }

            if (imagemList.size() == index + 1) {
                // Salva o Anúncio no Firebase
                salvarAnuncio();
            }

        })).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro ao salvar cadastro!", Toast.LENGTH_SHORT).show();
        });
    }

    // Salva o Anúncio no Firebase
    public void salvarAnuncio() {
        DatabaseReference firebaseRef = GetFirebase.getDatabase();
        DatabaseReference anuncioPublicoRef = firebaseRef
                .child("anunciosPublicos")
                .child(anuncio.getId());
        anuncioPublicoRef.setValue(anuncio);

        DatabaseReference meusAnuncioRef = firebaseRef
                .child("meusAnuncios")
                .child(GetFirebase.getIdFirebase())
                .child(anuncio.getId());
        meusAnuncioRef.setValue(anuncio).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            finish();
        });

        if (novoAnuncio) {
            DatabaseReference dataAnuncioPublico = anuncioPublicoRef
                    .child("dataCadastro");
            dataAnuncioPublico.setValue(ServerValue.TIMESTAMP);

            DatabaseReference dataMeusAnuncio = meusAnuncioRef
                    .child("dataCadastro");
            dataMeusAnuncio.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(task -> {
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("id", 2);
                startActivity(intent);
            });
        }
    }

    // Recupera Usuário do Firebase
    private void recuperaUsuario() {
        DatabaseReference perfilRef = GetFirebase.getDatabase()
                .child("usuarios")
                .child(GetFirebase.getIdFirebase());
        perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    usuario = snapshot.getValue(Usuario.class);

                    editCep.setText(usuario.getEndereco().getCep());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Configura as informações nos elementos
    private void configDados() {
        textToolbar.setText("Edição anúncio");

        categoriaSelecionada = anuncio.getCategoria();
        btnCategoria.setText(categoriaSelecionada);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        editNome.setText(anuncio.getNome());
        editPreco.setText(df.format(anuncio.getPreco()));
        editDescricao.setText(anuncio.getDescricao());

        Picasso.get().load(anuncio.getUrlFotos().get(0)).into(imagem0);
        Picasso.get().load(anuncio.getUrlFotos().get(1)).into(imagem1);
        Picasso.get().load(anuncio.getUrlFotos().get(2)).into(imagem2);

        novoAnuncio = false;
    }

    // Configura index das imagens
    private void configUpload(int requestCode, String caminho) {
        Imagem imagem = new Imagem(caminho, requestCode);
        if (imagemList.size() > 0) {

            boolean encontrou = false;
            for (int i = 0; i < imagemList.size(); i++) {

                if (imagemList.get(i).index == requestCode) {
                    encontrou = true;
                }

            }

            if (encontrou) { // já está na lista ( Atualiza )
                imagemList.set(requestCode, imagem);
            } else { // Não está na lista ( Adiciona )
                imagemList.add(imagem);
            }

        } else {
            imagemList.add(imagem);
        }

    }

    // Oculta o teclado do dispotivo
    private void ocultaTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editNome.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Bitmap bitmap0;
            Bitmap bitmap1;
            Bitmap bitmap2;

            Uri imagemSelecionada = data.getData();
            String caminho;

            // Seleção da categoria
            if (requestCode == REQUEST_CATEGORIA) {

                Categoria categoria = (Categoria) data.getSerializableExtra("categoriaSelecionada");
                if (categoria != null) {

                    categoriaSelecionada = categoria.getNome();

                    btnCategoria.setText(categoriaSelecionada);
                }

            } else if (requestCode <= 2) { // Seleção Galeria

                try {

                    //Recuperar caminho da imagem
                    caminho = imagemSelecionada.toString();

                    switch (requestCode) {
                        case 0:
                            if (Build.VERSION.SDK_INT < 28) {
                                bitmap0 = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), imagemSelecionada);
                            } else {
                                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imagemSelecionada);
                                bitmap0 = ImageDecoder.decodeBitmap(source);
                            }

                            imagem0.setImageBitmap(bitmap0);
                            break;
                        case 1:
                            if (Build.VERSION.SDK_INT < 28) {
                                bitmap1 = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), imagemSelecionada);
                            } else {
                                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imagemSelecionada);
                                bitmap1 = ImageDecoder.decodeBitmap(source);
                            }

                            imagem1.setImageBitmap(bitmap1);
                            break;
                        case 2:
                            if (Build.VERSION.SDK_INT < 28) {
                                bitmap2 = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), imagemSelecionada);
                            } else {
                                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imagemSelecionada);
                                bitmap2 = ImageDecoder.decodeBitmap(source);
                            }

                            imagem2.setImageBitmap(bitmap2);
                            break;
                    }

                    // Configura index das imagens
                    configUpload(requestCode, caminho);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else { // Seleção Camera

                try {

                    File f = new File(currentPhotoPath);

                    //Recuperar caminho da imagem
                    caminho = String.valueOf(f.toURI());

                    switch (requestCode) {
                        case 3:
                            imagem0.setImageURI(Uri.fromFile(f));
                            break;
                        case 4:
                            imagem1.setImageURI(Uri.fromFile(f));
                            break;
                        case 5:
                            imagem2.setImageURI(Uri.fromFile(f));
                            break;
                    }

                    // Configura index das imagens
                    configUpload(requestCode, caminho);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", new Locale("pt", "BR")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Abre a camera do Aparelho
    public void dispatchTakePictureIntent(int requestCode) {
        int request = 0;
        switch (requestCode) {
            case 0:
                request = 3;
                break;
            case 1:
                request = 4;
                break;
            case 2:
                request = 5;
                break;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ignored) {
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.olxclone.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, request);
        }
    }

    // Solicita Permissão Galeria
    private void verificaPermissaoGaleria(int requestCode) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                escolherImagemGaleria(requestCode);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormAnuncioActivity.this, "Permissão Negada", Toast.LENGTH_SHORT).show();
            }

        };

        // Exibe Dialog Permissão Negada
        showDialogPermissao(permissionlistener,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                "Se você não aceitar a permissão não poderá acessar a Galeria do dispositivo, deseja ativar a permissão agora ?"
        );

    }

    // Solicita Permissão Camera
    private void verificaPermissaoCamera(int requestCode) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                dispatchTakePictureIntent(requestCode);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormAnuncioActivity.this, "Permissão Negada", Toast.LENGTH_SHORT).show();
            }

        };

        // Exibe Dialog Permissão Negada
        showDialogPermissao(permissionlistener,
                new String[]{Manifest.permission.CAMERA},
                "Se você não aceitar a permissão não poderá acessar a Camera do dispositivo, deseja ativar a permissão agora ?"
        );

    }

    // Exibe Dialog Permissão Negada
    private void showDialogPermissao(PermissionListener permissionlistener, String[] permissoes, String msg){
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("Permissões")
                .setDeniedMessage(msg)
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    // Inicia componentes de tela
    private void iniciaComponentes() {
        textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Novo anúncio");

        imagem0 = findViewById(R.id.imagem0);
        imagem1 = findViewById(R.id.imagem1);
        imagem2 = findViewById(R.id.imagem2);

        editNome = findViewById(R.id.editNome);

        editPreco = findViewById(R.id.editPreco);
        editPreco.setLocale(new Locale("pt", "BR"));

        btnCategoria = findViewById(R.id.btnCategoria);
        editDescricao = findViewById(R.id.editDescricao);
        editCep = findViewById(R.id.editCep);
        textLocal = findViewById(R.id.textLocal);
        progressBar = findViewById(R.id.progressBar);
    }

}