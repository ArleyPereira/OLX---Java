package com.example.olxclone.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxclone.R;
import com.example.olxclone.Util.SPFiltro;
import com.example.olxclone.activity.CategoriasActivity;
import com.example.olxclone.activity.DetalheAnuncioActivity;
import com.example.olxclone.activity.EstadosActivity;
import com.example.olxclone.activity.FiltrosActivity;
import com.example.olxclone.activity.FormAnuncioActivity;
import com.example.olxclone.adapter.AdapterAnuncio;
import com.example.olxclone.activity.autenticacao.LoginActivity;
import com.example.olxclone.helper.GetFirebase;
import com.example.olxclone.model.Anuncio;
import com.example.olxclone.model.Filtro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterAnuncio.OnItemClick {

    private Button btnRegiao;
    private Button btnCategoria;
    private Button btnFiltros;
    private Button btnInserir;

    private Filtro filtro = new Filtro();

    private ProgressBar progressBar;
    private TextView textInfo;

    private RecyclerView rvAnuncios;
    private final List<Anuncio> anuncioList = new ArrayList<>();
    private AdapterAnuncio adapterAnuncio;

    private LinearLayout ltPesquisa;
    private TextView textPesquisa;
    private TextView textLimparPesquisa;
    private EditText edtSearch;
    private SearchView searchView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicia componentes de tela
        iniciaComponentes(view);

        // Configura SearchView
        configSearchView();

        // Ouvinte Cliques
        configCliques();

        // Configuração Iniciais Recycler
        configRecycler();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Verifica se tem algum filtro
        configFiltro();
    }

    // Configura SearchView
    private void configSearchView() {
        edtSearch = searchView.findViewById(R.id.search_src_text);
        searchView.setQueryHint("Estou procurando por...");
        edtSearch.setTextColor(Color.parseColor("#000000"));
        edtSearch.setTextSize(16);

        searchView.findViewById(R.id.search_close_btn)
                .setOnClickListener(view -> {

                    // Limpa a pesquisa salva em SharedPreferences
                    limparPesquisa();

                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SPFiltro.setFiltro(getActivity(), "pesquisa", newText);

                return true;
            }
        });

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                // Verifica se tem algum filtro
                configFiltro();

                // Oculta o teclado do dispotivo
                ocultaTeclado();

                return true;
            }
            return false;
        });
    }

    // Limpa a pesquisa salva em SharedPreferences
    private void limparPesquisa(){
        // Fecha o teclado
        searchView.clearFocus();

        // Limpa o texto
        edtSearch.setText("");

        // Limpa a pesquisa salva em SharedPreferences
        SPFiltro.setFiltro(getActivity(), "pesquisa", "");

        // Verifica se tem algum filtro
        configFiltro();
    }

    // Verifica se tem algum filtro
    private void configFiltro() {
        filtro = SPFiltro.getFiltro(getActivity());

        if (!filtro.getCategoria().isEmpty()) {
            btnCategoria.setText(filtro.getCategoria());
        }else {
            btnCategoria.setText("Categorias");
        }

        if (!filtro.getEstado().getRegiao().isEmpty()) {
            btnRegiao.setText(filtro.getEstado().getRegiao());
        } else if (!filtro.getEstado().getNome().equals("Brasil")) {
            if (!filtro.getEstado().getNome().isEmpty()) {
                btnRegiao.setText(filtro.getEstado().getNome());
            } else {
                btnRegiao.setText("Região");
            }
        }

        if(filtro.getPesquisa().isEmpty()){
            ltPesquisa.setVisibility(View.GONE);
            textPesquisa.setText("");
        }else {
            textPesquisa.setText("Pesquisa: " + filtro.getPesquisa());
            ltPesquisa.setVisibility(View.VISIBLE);
        }

        // Recupera Anúncios do Firebase
        recuperaAnuncios();

    }

    // Recupera Anúncios do Firebase
    private void recuperaAnuncios() {
        DatabaseReference filtroRef = GetFirebase.getDatabase()
                .child("anunciosPublicos");
        filtroRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {

                    progressBar.setVisibility(View.VISIBLE);

                    anuncioList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        anuncioList.add(ds.getValue(Anuncio.class));
                    }

                    // Filtra pela categoria selecionada
                    if (!filtro.getCategoria().isEmpty()) {
                        if (!filtro.getCategoria().equals("Todas as categorias")) {
                            for (Anuncio anuncio : new ArrayList<>(anuncioList)) {
                                if (!anuncio.getCategoria().equals(filtro.getCategoria())) {
                                    anuncioList.remove(anuncio);
                                }
                            }
                        }
                    }

                    // Filtra pelo UF do estado selecionado
                    if (!filtro.getEstado().getUf().isEmpty()) {
                        for (Anuncio anuncio : new ArrayList<>(anuncioList)) {
                            if (!anuncio.getLocal().getUf().equals(filtro.getEstado().getUf())) {
                                anuncioList.remove(anuncio);
                            }
                        }
                    }

                    // Filtra pelo DDD selecionado
                    if (!filtro.getEstado().getDdd().isEmpty()) {
                        for (Anuncio anuncio : new ArrayList<>(anuncioList)) {
                            if (!anuncio.getLocal().getDdd().equals(filtro.getEstado().getDdd())) {
                                anuncioList.remove(anuncio);
                            }
                        }
                    }

                    // Filtra pelo nome pesquisado
                    if (!filtro.getPesquisa().isEmpty()) {
                        for (Anuncio anuncio : new ArrayList<>(anuncioList)) {
                            if (!anuncio.getNome().toLowerCase().contains(filtro.getPesquisa().toLowerCase())) {
                                anuncioList.remove(anuncio);
                            }
                        }
                    }

                    // Filtra pelo valor mínimo digitado
                    if (filtro.getValorMin() != 0) {
                        for (Anuncio anuncio : new ArrayList<>(anuncioList)) {
                            if (anuncio.getPreco() < filtro.getValorMin()) {
                                anuncioList.remove(anuncio);
                            }
                        }
                    }

                    // Filtra pelo valor máximo digitado
                    if (filtro.getValorMax() != 0) {
                        for (Anuncio anuncio : new ArrayList<>(anuncioList)) {
                            if (anuncio.getPreco() > filtro.getValorMax()) {
                                anuncioList.remove(anuncio);
                            }
                        }
                    }

                    if (anuncioList.size() == 0) {
                        textInfo.setText("Nenhum Anúncio encontrado.");
                    } else {
                        textInfo.setText("");
                    }

                    Collections.reverse(anuncioList);
                    progressBar.setVisibility(View.GONE);
                    adapterAnuncio.notifyDataSetChanged();

                } else {
                    textInfo.setText("Nenhum Anúncio Cadastrado.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Configuração Iniciais Recycler
    private void configRecycler() {
        rvAnuncios.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAnuncios.setHasFixedSize(true);
        adapterAnuncio = new AdapterAnuncio(anuncioList, this, requireActivity());
        rvAnuncios.setAdapter(adapterAnuncio);
    }

    // Ouvinte Cliques
    private void configCliques() {
        btnInserir.setOnClickListener(view -> {
            if (GetFirebase.getAutenticado()) {
                startActivity(new Intent(getActivity(), FormAnuncioActivity.class));
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        btnCategoria.setOnClickListener(view -> startActivity(new Intent(getActivity(), CategoriasActivity.class)));
        btnRegiao.setOnClickListener(view -> startActivity(new Intent(getActivity(), EstadosActivity.class)));
        btnFiltros.setOnClickListener(view -> startActivity(new Intent(getActivity(), FiltrosActivity.class)));
        textLimparPesquisa.setOnClickListener(view -> limparPesquisa());
    }

    // Oculta o teclado do dispotivo
    private void ocultaTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Inicia componentes de tela
    private void iniciaComponentes(View view) {
        ltPesquisa = view.findViewById(R.id.ltPesquisa);
        textLimparPesquisa = view.findViewById(R.id.textLimparPesquisa);
        textPesquisa = view.findViewById(R.id.textPesquisa);
        searchView = view.findViewById(R.id.searchView);

        btnRegiao = view.findViewById(R.id.btnRegiao);
        btnCategoria = view.findViewById(R.id.btnCategoria);
        btnFiltros = view.findViewById(R.id.btnFiltros);
        btnInserir = view.findViewById(R.id.btnInserir);
        progressBar = view.findViewById(R.id.progressBar);
        textInfo = view.findViewById(R.id.textInfo);
        rvAnuncios = view.findViewById(R.id.rvAnuncios);
    }

    @Override
    public void itemClick(Anuncio anuncio) {
        Intent intent = new Intent(getActivity(), DetalheAnuncioActivity.class);
        intent.putExtra("anuncioSelecionado", anuncio);
        startActivity(intent);
    }

}