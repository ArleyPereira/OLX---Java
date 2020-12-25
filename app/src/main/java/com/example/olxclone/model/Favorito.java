package com.example.olxclone.model;

import com.example.olxclone.helper.GetFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Favorito {

    private List<String> favoritos;

    public void salvar(){
        DatabaseReference firebaseRef = GetFirebase.getDatabase();
        DatabaseReference favoritosRef = firebaseRef
                .child("favoritos")
                .child(GetFirebase.getIdFirebase());
        favoritosRef.setValue(getFavoritos());
    }

    public List<String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<String> favoritos) {
        this.favoritos = favoritos;
    }

}
