package com.example.olxclone.model;

import com.example.olxclone.helper.GetFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Anuncio implements Serializable {

    private String id;
    private String nome;
    private double preco;
    private Local local;
    private String descricao;
    private String idUsuario;
    private String categoria;
    private long dataCadastro;
    private List<String> urlFotos = new ArrayList<>();

    public Anuncio() {
        DatabaseReference firebaseRef = GetFirebase.getDatabase();
        DatabaseReference anuncioRef = firebaseRef
                .child("anunciosPublicos");
        setId(anuncioRef.push().getKey());
    }

    public void remover() {

        DatabaseReference firebaseRef = GetFirebase.getDatabase();
        DatabaseReference anuncioPublicoRef = firebaseRef
                .child("anunciosPublicos")
                .child(getId());
        anuncioPublicoRef.removeValue();

        DatabaseReference meusAnuncioRef = firebaseRef
                .child("meusAnuncios")
                .child(getIdUsuario())
                .child(getId());
        meusAnuncioRef.removeValue();

        StorageReference storage = GetFirebase.getStorage();

        for (int i = 0; i < getUrlFotos().size(); i++) {
            StorageReference imagemAnuncio = storage.child("imagens")
                    .child("anuncios")
                    .child(getId())
                    .child("imagem" + i + ".jpeg");
            imagemAnuncio.delete();
        }


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public long getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(long dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<String> getUrlFotos() {
        return urlFotos;
    }

    public void setUrlFotos(List<String> urlFotos) {
        this.urlFotos = urlFotos;
    }
}
