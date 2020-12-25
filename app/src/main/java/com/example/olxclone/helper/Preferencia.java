package com.example.olxclone.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencia {

    public static void salvar(Context context, String chave, String valor){
        SharedPreferences preferences = context.getSharedPreferences("preferencia", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(chave, valor);
        editor.apply();
    }

    public static String recupera(Context context, String chave){
        SharedPreferences preferences = context.getSharedPreferences("preferencia", 0);
        return preferences.getString(chave, null);
    }

}
