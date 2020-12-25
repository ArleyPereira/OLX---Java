package com.example.olxclone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olxclone.R;
import com.example.olxclone.Util.GetMask;
import com.example.olxclone.activity.autenticacao.LoginActivity;
import com.example.olxclone.adapter.SliderAdapter;
import com.example.olxclone.helper.GetFirebase;
import com.example.olxclone.model.Anuncio;
import com.example.olxclone.model.Favorito;
import com.example.olxclone.model.Usuario;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class DetalheAnuncioActivity extends AppCompatActivity {

    private ImageButton ibLigar;
    private LikeButton likeButton;

    private TextView textTitle;
    private TextView textPrice;
    private TextView textDescription;
    private TextView textCep;
    private TextView textMunicipio;
    private TextView textBairro;
    private TextView textCagegory;
    private TextView textPublicado;

    private final List<String> favoritos = new ArrayList<>();

    private Anuncio anuncio;
    private Usuario usuario;

    private SliderView sliderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_anuncio);

        // Inicia componentes de tela
        iniciaComponentes();

        anuncio = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");
        if (anuncio != null) configDados();

        // Ouvinte Cliques
        configCliques();

        // Ouvinte Like Button
        configLikeButton();

        // Recupera informações do Usuário dono do Anúncio
        recuperaUserAnuncio();

    }

    // Ouvinte Cliques
    private void configCliques() {
        findViewById(R.id.ibVoltar).setOnClickListener(view -> finish());
        ibLigar.setOnClickListener(view -> ligar());
    }

    // Recupera informações do Usuário dono do Anúncio
    private void recuperaUserAnuncio() {
        DatabaseReference userRef = GetFirebase.getDatabase()
                .child("usuarios")
                .child(anuncio.getIdUsuario());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    usuario = dataSnapshot.getValue(Usuario.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Configura o estado do Like Button
    private void configuraFavorito() {
        if (GetFirebase.getAutenticado()) {
            DatabaseReference favoritoRef = GetFirebase.getDatabase()
                    .child("favoritos")
                    .child(GetFirebase.getIdFirebase());
            favoritoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            favoritos.add(ds.getValue(String.class));
                        }
                    }

                    if (favoritos.contains(anuncio.getId())) {
                        likeButton.setLiked(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    // Ouvinte Like Button
    private void configLikeButton() {
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (GetFirebase.getAutenticado()) {

                    // Exibe SnackBar Custom
                    configSnackBar(
                            "DESFAZER",
                            "Anúncio salvo",
                            R.drawable.ic_favorito_like_red, true);

                } else {
                    likeButton.setLiked(false);

                    // Dialog Usuário não ligado
                    alertaAutenticacao();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {

                // Exibe SnackBar Custom
                configSnackBar(
                        "",
                        "Anúncio removido",
                        R.drawable.ic_favorito_unlike, false);

            }
        });
    }

    // Exibe SnackBar Custom
    private void configSnackBar(String actionMsg, String msg, int icon, boolean like) {
        // Configura status do Firito
        configFavorito(like);

        Snackbar snackbar = Snackbar.make(likeButton, msg, Snackbar.LENGTH_SHORT);
        snackbar.setAction(actionMsg, view -> {
            // Configura status do Firito
            configFavorito(false);
        });

        TextView textView = snackbar.getView().findViewById(R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        textView.setCompoundDrawablePadding(24);
        snackbar.setActionTextColor(Color.parseColor("#F78323"))
                .setTextColor(Color.parseColor("#FFFFFF"))
                .show();
    }

    // Configura status do Firito
    private void configFavorito(boolean like) {
        if (like) {
            likeButton.setLiked(true);
            favoritos.add(anuncio.getId());
        } else {
            likeButton.setLiked(false);
            favoritos.remove(anuncio.getId());
        }
        Favorito favorito = new Favorito();
        favorito.setFavoritos(favoritos);
        favorito.salvar();
    }

    // Abre o aplicativo de chamadas do aparelho
    private void ligar() {
        if (GetFirebase.getAutenticado()) {
            if (usuario != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", usuario.getTelefone(), null));
                startActivity(intent);
            }
        } else {
            alertaAutenticacao();
        }
    }

    // Dialog Usuário não ligado
    private void alertaAutenticacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção");
        builder.setMessage("Para entrar em contato com anunciantes é preciso está logado no app.");
        builder.setCancelable(false);
        builder.setNegativeButton("Entendi", null);
        builder.setPositiveButton("Fazer login", (dialog, which) ->
                startActivity(new Intent(this, LoginActivity.class)));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Configura os dados nos elementos
    private void configDados() {
        if (GetFirebase.getAutenticado()) {
            if (GetFirebase.getIdFirebase().equals(anuncio.getIdUsuario())) {
                ibLigar.setVisibility(View.GONE);
                likeButton.setVisibility(View.GONE);
            }
        }

        // Configura o estado do Like Button
        configuraFavorito();

        sliderView.setSliderAdapter(new SliderAdapter(anuncio.getUrlFotos()));
        sliderView.startAutoCycle();
        sliderView.setScrollTimeInSec(4);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

        textPrice.setText(getString(R.string.valor, GetMask.getValor(anuncio.getPreco())));

        String categoria = anuncio.getCategoria();
        if (categoria.equals("Farmácia Solidária") || categoria.equals("Adote um Bichinho")) {
            textPrice.setVisibility(View.GONE);
        }

        textTitle.setText(anuncio.getNome());
        textPublicado.setText(getString(R.string.data_publicacao, GetMask.getDate(anuncio.getDataCadastro(), 3)));
        textDescription.setText(anuncio.getDescricao());
        textCagegory.setText(anuncio.getCategoria());

        textCep.setText(anuncio.getLocal().getCep());
        textMunicipio.setText(anuncio.getLocal().getLocalidade());
        textBairro.setText(anuncio.getLocal().getBairro());

    }

    // Inicia componentes de tela
    private void iniciaComponentes() {
        ibLigar = findViewById(R.id.ibLigar);
        textTitle = findViewById(R.id.textTitle);
        likeButton = findViewById(R.id.likeButton);
        sliderView = findViewById(R.id.sliderView);
        textCagegory = findViewById(R.id.textCagegory);
        textDescription = findViewById(R.id.textDescription);
        textPrice = findViewById(R.id.textPrice);
        textPublicado = findViewById(R.id.textPublicado);
        textCep = findViewById(R.id.textCep);
        textMunicipio = findViewById(R.id.textMunicipio);
        textBairro = findViewById(R.id.textBairro);
    }

}