package com.example.olxclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olxclone.R;
import com.example.olxclone.Util.CategoriaList;
import com.example.olxclone.Util.SPFiltro;
import com.example.olxclone.adapter.AdapterCategoria;
import com.example.olxclone.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriasActivity extends AppCompatActivity implements AdapterCategoria.OnItemClick {

    private boolean todasCategorias = true;

    private RecyclerView rvCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            todasCategorias = (Boolean) bundle.getSerializable("todasCategorias");
        }

        // Inicia componentes de tela
        iniciaComponentes();

        // Configura as categorias
        configCategorias();

    }

    // Configura as categorias
    private void configCategorias(){
        rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        rvCategorias.setHasFixedSize(true);
        AdapterCategoria adapterCategoria = new AdapterCategoria(CategoriaList.getList(todasCategorias), this);
        rvCategorias.setAdapter(adapterCategoria);

    }

    // Inicia componentes de tela
    private void iniciaComponentes(){
        findViewById(R.id.ibVoltar).setOnClickListener(view -> finish());

        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Categorias");

        rvCategorias = findViewById(R.id.rvCategorias);
    }

    @Override
    public void itemClick(Categoria categoria) {
        if(!categoria.getNome().equals("Todas as Categorias")){
            SPFiltro.setFiltro(this, "categoria", categoria.getNome());
        }else {
            SPFiltro.setFiltro(this, "categoria", "");
        }
        finish();
    }

}