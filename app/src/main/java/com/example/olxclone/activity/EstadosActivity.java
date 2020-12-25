package com.example.olxclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxclone.R;
import com.example.olxclone.Util.EstadoList;
import com.example.olxclone.Util.SPFiltro;
import com.example.olxclone.adapter.AdapterEstado;
import com.example.olxclone.model.Estado;

public class EstadosActivity extends AppCompatActivity implements AdapterEstado.OnItemClick {

    // Se o acesso partiu da tela de filtro
    private boolean filtro = false;

    private RecyclerView rvEstados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estados);

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
    private void configCliques() {
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    // Configura Recycler Estados
    private void configRecycler() {
        rvEstados.setLayoutManager(new LinearLayoutManager(this));
        rvEstados.setHasFixedSize(true);
        AdapterEstado adapterEstado = new AdapterEstado(EstadoList.getList(), this);
        rvEstados.setAdapter(adapterEstado);
    }

    @Override
    public void itemClick(Estado estado) {
        if (!estado.getNome().equals("Brasil")) {
            SPFiltro.setFiltro(this, "ufEstado", estado.getUf());
            SPFiltro.setFiltro(this, "ddd", estado.getDdd());
            SPFiltro.setFiltro(this, "nomeEstado", estado.getNome());
            if(!filtro){
                startActivity(new Intent(this, RegioesActivity.class));
            }else {
                finish();
            }
        } else {
            SPFiltro.setFiltro(this, "ufEstado", "");
            SPFiltro.setFiltro(this, "ddd", "");
            SPFiltro.setFiltro(this, "regiao", "");
            SPFiltro.setFiltro(this, "nomeEstado", "");
            finish();
        }
    }

    // Inicia componentes de tela
    private void iniciaComponentes() {
        rvEstados = findViewById(R.id.rvEstados);
        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Estados");
    }

}