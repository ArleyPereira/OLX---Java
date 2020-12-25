package com.example.olxclone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.olxclone.R;
import com.example.olxclone.activity.FormEnderecoActivity;
import com.example.olxclone.activity.MainActivity;
import com.example.olxclone.activity.PerfilActivity;
import com.example.olxclone.activity.autenticacao.LoginActivity;
import com.example.olxclone.helper.GetFirebase;
import com.example.olxclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ContaFragment extends Fragment {

    private ImageView imagemPerfil;
    private TextView textConta;
    private TextView textNome;

    private Button btnPerfil;
    private Button btnEndereco;

    private Usuario usuario;

    public ContaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_conta, container, false);

        // Inicia componentes
        iniciaComponentes(view);

        // Ouvinte Cliques
        configCliques();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Recupera Email e nome do Usuário logado
        recuperaUsuario();
    }

    // Recupera Email e nome do Usuário logado
    private void recuperaUsuario() {
        if (GetFirebase.getAutenticado()) {
            DatabaseReference usuarioRef = GetFirebase.getDatabase()
                    .child("usuarios")
                    .child(GetFirebase.getIdFirebase());
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usuario = snapshot.getValue(Usuario.class);

                    if (usuario != null) {
                        // Configura Email e nome do Usuário logado
                        configConta();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            imagemPerfil.setScaleType(ImageView.ScaleType.CENTER);
            imagemPerfil.setImageResource(R.drawable.ic_user_cinza);
        }
    }

    // Configura Email e nome do Usuário logado
    private void configConta() {
        textNome.setText(usuario.getNome());
        if (usuario.getUrlImagem() != null) {
            Picasso.get().load(usuario.getUrlImagem())
                    .placeholder(R.drawable.loading)
                    .into(imagemPerfil);
        }else {
            imagemPerfil.setScaleType(ImageView.ScaleType.CENTER);
            imagemPerfil.setImageResource(R.drawable.ic_user_cinza);
        }
    }

    // Ouvinte Cliques
    private void configCliques() {
        if (!GetFirebase.getAutenticado()) {
            textNome.setText("Acesse sua conta agora!");
            textConta.setText("Clique aqui");
        } else {
            textConta.setText("Sair");
        }

        btnPerfil.setOnClickListener(view -> redirecionaAcesso(PerfilActivity.class));

        btnEndereco.setOnClickListener(view -> redirecionaAcesso(FormEnderecoActivity.class));

        textConta.setOnClickListener(view -> {
            if (GetFirebase.getAutenticado()) {
                GetFirebase.getAuth().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }

    // Redireciona o acesso
    private void redirecionaAcesso(Class<?> clazz) {
        if (GetFirebase.getAutenticado()) {
            startActivity(new Intent(getActivity(), clazz));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    // Inicia componentes de tela
    private void iniciaComponentes(View view) {
        imagemPerfil = view.findViewById(R.id.imagemPerfil);
        textConta = view.findViewById(R.id.textConta);
        textNome = view.findViewById(R.id.textNome);
        btnPerfil = view.findViewById(R.id.btnPerfil);
        btnEndereco = view.findViewById(R.id.btnEndereco);
    }

}
