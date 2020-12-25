package com.example.olxclone.model;

import java.io.Serializable;

public class Categoria implements Serializable {

    private int imagem;
    private String nome;

    public Categoria(int imagem, String nome) {
        this.imagem = imagem;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }
}
