package com.example.olxclone.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.olxclone.model.Estado;
import com.example.olxclone.model.Filtro;

public class SPFiltro {

    private static final String ARQUIVO_PREFERENCIA = "ArquivoPreferencia";

    // Salva Filtro em SharedPreferences
    public static void setFiltro(Activity activity, String chave, String valor){
        SharedPreferences preferences = activity.getSharedPreferences(ARQUIVO_PREFERENCIA, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(chave, valor);
        editor.apply();
    }

    // Recupera Filtro em SharedPreferences
    public static Filtro getFiltro(Activity activity){

        SharedPreferences preferences = activity.getSharedPreferences(ARQUIVO_PREFERENCIA, 0);

        String pesquisa = preferences.getString("pesquisa", "");
        String ufEstado = preferences.getString("ufEstado", "");
        String categoria = preferences.getString("categoria", "");
        String nomeEstado = preferences.getString("nomeEstado", "");
        String regiao = preferences.getString("regiao", "");
        String ddd = preferences.getString("ddd", "");

        String min = preferences.getString("valorMin", "");
        String max = preferences.getString("valorMax", "");

        Estado estado = new Estado();
        estado.setNome(nomeEstado);
        estado.setUf(ufEstado);
        estado.setRegiao(regiao);
        estado.setDdd(ddd);

        Filtro filtro = new Filtro();
        filtro.setPesquisa(pesquisa);
        filtro.setEstado(estado);
        filtro.setCategoria(categoria);

        if(!min.isEmpty()) filtro.setValorMin(Integer.parseInt(min));
        if(!max.isEmpty()) filtro.setValorMax(Integer.parseInt(max));

        return filtro;
    }

    // Limpa todos os Filtros em SharedPreferences
    public static void limpaFiltros(Activity activity){
        SPFiltro.setFiltro(activity, "pesquisa", "");
        SPFiltro.setFiltro(activity, "nomeEstado", "");
        SPFiltro.setFiltro(activity, "ufEstado", "");
        SPFiltro.setFiltro(activity, "categoria", "");
        SPFiltro.setFiltro(activity, "regiao", "");
        SPFiltro.setFiltro(activity, "ddd", "");
        SPFiltro.setFiltro(activity, "valorMin", "");
        SPFiltro.setFiltro(activity, "valorMax", "");
    }

}
