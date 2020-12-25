package com.example.olxclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxclone.R;
import com.example.olxclone.Util.SPFiltro;
import com.example.olxclone.model.Filtro;

import java.util.Locale;

public class FiltrosActivity extends AppCompatActivity {

    private CurrencyEditText editValorMin;
    private CurrencyEditText editValorMax;

    private Button btnCategoria;
    private Button btnEstado;
    private Button btnRegiao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        // Inicia componentes de tela
        iniciaComponentes();

        // Ouvinte Cliques
        configCliques();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Configura os dados nos elementos
        configDados();
    }

    // Configura os dados nos elementos
    private void configDados(){

        Filtro filtro = SPFiltro.getFiltro(this);

        // Categoria
        if(!filtro.getCategoria().isEmpty()){
            btnCategoria.setText(filtro.getCategoria());
        }else {
            btnCategoria.setText("Todas as categorias");
        }

        // Valor mínimo
        if(filtro.getValorMin() != 0){
            editValorMin.setValue(filtro.getValorMin() * 100);
        }else {
            editValorMin.setValue(0);
        }

        // Valor máximo
        if(filtro.getValorMax() != 0){
            editValorMax.setValue(filtro.getValorMax() * 100);
        }else {
            editValorMax.setValue(0);
        }

        // Estado
        if(!filtro.getEstado().getNome().isEmpty()){
            btnEstado.setText(filtro.getEstado().getNome());

            btnRegiao.setVisibility(View.VISIBLE);
        }else {
            btnEstado.setText("Todos os estados");

            btnRegiao.setVisibility(View.GONE);
        }

        // Região
        if(!filtro.getEstado().getRegiao().isEmpty()){
            btnRegiao.setText(filtro.getEstado().getRegiao());
        }else {
            btnRegiao.setText("Todas as regiões");
        }

    }

    // Configura Valor Máximo e Minímo
    private void configValores(){
        String valorMin = String.valueOf(editValorMin.getRawValue() / 100);
        String valorMax = String.valueOf(editValorMax.getRawValue() / 100);

        SPFiltro.setFiltro(this, "valorMin", valorMin);
        SPFiltro.setFiltro(this, "valorMax", valorMax);
    }

    // Limpa Todos os Filtros
    private void limparFiltros(){
        SPFiltro.limpaFiltros(this);

        // Configura os dados nos elementos
        configDados();
    }

    // Leva o Usuário para tela principal
    public void filtrar(View view){

        // Configura Valor Máximo e Minímo
        configValores();

        finish();
    }

    // Ouvinte Cliques
    private void configCliques(){
        findViewById(R.id.ibVoltar).setOnClickListener(view -> finish());
        findViewById(R.id.btnLimpar).setOnClickListener(view -> limparFiltros());
    }

    // Selecionar o Estado
    public void selecinoarEstado(View view){

        // Configura Valor Máximo e Minímo
        configValores();

        Intent intent = new Intent(this, EstadosActivity.class);
        intent.putExtra("filtro", true);
        startActivity(intent);
    }

    // Selecionar a Região
    public void selecinoarRegiao(View view){
        Intent intent = new Intent(this, RegioesActivity.class);
        intent.putExtra("filtro", true);
        startActivity(intent);
    }

    // Selecionar a Categoria
    public void selecionarCategoria(View view){
        // Configura Valor Máximo e Minímo
        configValores();

        startActivity(new Intent(this, CategoriasActivity.class));
    }

    // Inicia componentes de tela
    private void iniciaComponentes(){
        TextView textToolbar = findViewById(R.id.textToolbar);
        textToolbar.setText("Filtros");

        btnCategoria = findViewById(R.id.btnCategoria);
        btnEstado = findViewById(R.id.btnEstado);
        btnRegiao = findViewById(R.id.btnRegiao);

        editValorMin = findViewById(R.id.editValorMin);
        editValorMin.setLocale(new Locale("pt", "BR"));

        editValorMax = findViewById(R.id.editValorMax);
        editValorMax.setLocale(new Locale("pt", "BR"));
    }

}