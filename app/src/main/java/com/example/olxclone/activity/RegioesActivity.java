package com.example.olxclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxclone.R;
import com.example.olxclone.Util.RegiaoList;
import com.example.olxclone.Util.SPFiltro;
import com.example.olxclone.adapter.AdapterRegiao;
import com.example.olxclone.model.Filtro;

public class RegioesActivity extends AppCompatActivity implements AdapterRegiao.OnItemClick {

    // Se o acesso partiu da tela de filtro
    private boolean filtro = false;

    private RecyclerView rvRegioes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regioes);

        // Inicia componentes de tela
        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            filtro = (Boolean) bundle.getSerializable("filtro");
        }

        // Configura Recycler Estados
        configRecycler();

        // Ouvinte Cliques
        configCliques();

    }

    // Ouvinte Cliques
    private void configCliques(){
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    // Inicia componentes de tela
    private void iniciaComponentes(){
        rvRegioes = findViewById(R.id.rvRegioes);
        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Regiões");
    }

    // Configura Recycler Regiões
    private void configRecycler(){

        Filtro filtro = SPFiltro.getFiltro(this);

        rvRegioes.setLayoutManager(new LinearLayoutManager(this));
        rvRegioes.setHasFixedSize(true);
        AdapterRegiao adapterRegiao = new AdapterRegiao(RegiaoList.getRegioes(filtro.getEstado().getUf()), this);
        rvRegioes.setAdapter(adapterRegiao);
    }

    @Override
    public void itemClick(String regiao) {
        if(!regiao.equals("Todas as regiões")){
            SPFiltro.setFiltro(this, "ddd", regiao.substring(4, 6));
            SPFiltro.setFiltro(this, "regiao", regiao);
        }else {
            SPFiltro.setFiltro(this, "regiao", "");
            SPFiltro.setFiltro(this, "ddd", "");
        }

        if(!filtro){
            startActivity(new Intent(this, MainActivity.class));
        }else {
            finish();
        }
    }

}