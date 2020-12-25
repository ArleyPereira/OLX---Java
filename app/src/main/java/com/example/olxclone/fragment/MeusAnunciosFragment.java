package com.example.olxclone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.olxclone.R;
import com.example.olxclone.activity.FormAnuncioActivity;
import com.example.olxclone.adapter.AdapterAnuncio;
import com.example.olxclone.activity.autenticacao.LoginActivity;
import com.example.olxclone.helper.GetFirebase;
import com.example.olxclone.model.Anuncio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeusAnunciosFragment extends Fragment implements AdapterAnuncio.OnItemClick {

    private ProgressBar progressBar;
    private TextView textInfo;
    private Button btnLogin;

    private SwipeableRecyclerView rvAnuncios;
    private final List<Anuncio> anuncioList = new ArrayList<>();
    private AdapterAnuncio adapterAnuncio;


    public MeusAnunciosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meus_anuncios, container, false);

        // Inicia componentes de tela
        iniciaComponentes(view);

        // Configuração Iniciais Recycler
        configRecycler();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Recupera anúncios do Firebase
        recuperaAnuncio();
    }

    // Recupera anúncios do Firebase
    private void recuperaAnuncio(){
        if(GetFirebase.getAutenticado()){
            DatabaseReference firebaseRef = GetFirebase.getDatabase();
            DatabaseReference anuncioRef = firebaseRef
                    .child("meusAnuncios")
                    .child(GetFirebase.getIdFirebase());
            anuncioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.getValue() != null){

                        anuncioList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            anuncioList.add(ds.getValue(Anuncio.class));
                        }

                    }

                    if(anuncioList.size() > 0){
                        textInfo.setText("");
                    }else {
                        textInfo.setText("Você ainda não possui nenhum anúncio cadastrado.");
                    }

                    Collections.reverse(anuncioList);
                    progressBar.setVisibility(View.GONE);
                    adapterAnuncio.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            progressBar.setVisibility(View.GONE);
            textInfo.setText("Você não está autenticado no app.");

            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setOnClickListener(view -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        }
    }

    // Configuração Iniciais Recycler
    private void configRecycler(){
        rvAnuncios.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAnuncios.setHasFixedSize(true);
        adapterAnuncio = new AdapterAnuncio(anuncioList, this, requireActivity());
        rvAnuncios.setAdapter(adapterAnuncio);

        rvAnuncios.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                removerAnuncio(anuncioList.get(position));
            }

            @Override
            public void onSwipedRight(int position) {

                // Exibe Dialog para editar do anúncio
                editAnuncio(anuncioList.get(position));

            }
        });
    }

    // Exibe Dialog para remoção do anúncio
    private void removerAnuncio(Anuncio anuncio){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deseja remover este anúncio ?");
        builder.setMessage("Aperte em sim para confirmar ou aperte em não para sair.");
        builder.setNegativeButton("Não", ((dialog, which) -> {

            dialog.dismiss();
            adapterAnuncio.notifyDataSetChanged();

        })).setPositiveButton("Sim", ((dialog, which) -> {

            anuncioList.remove(anuncio);
            anuncio.remover();

            dialog.dismiss();
            adapterAnuncio.notifyDataSetChanged();

        }));

        if(anuncioList.size() == 0){
            textInfo.setText("Você ainda não possui nenhum anúncio cadastrado.");
        }

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    // Exibe Dialog para editar do anúncio
    private void editAnuncio(Anuncio anuncio){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deseja editar este anúncio ?");
        builder.setMessage("Aperte em sim para confirmar ou aperte em não para sair.");
        builder.setNegativeButton("Não", ((dialog, which) -> {

            dialog.dismiss();
            adapterAnuncio.notifyDataSetChanged();

        })).setPositiveButton("Sim", ((dialog, which) -> {

            Intent intent = new Intent(getActivity(), FormAnuncioActivity.class);
            intent.putExtra("anuncioSelecionado", anuncio);
            startActivity(intent);

            adapterAnuncio.notifyDataSetChanged();

        }));

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    // Inicia componentes de tela
    private void iniciaComponentes(View view){
        progressBar = view.findViewById(R.id.progressBar);
        textInfo = view.findViewById(R.id.textInfo);
        rvAnuncios = view.findViewById(R.id.rvAnuncios);
        btnLogin = view.findViewById(R.id.btnLogin);
    }

    @Override
    public void itemClick(Anuncio anuncio) {
    }

}