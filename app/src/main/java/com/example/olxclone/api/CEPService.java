package com.example.olxclone.api;

import com.example.olxclone.model.Endereco;
import com.example.olxclone.model.Local;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CEPService {

    @GET("{cep}/json/")
    Call<Local> recuperarCEP(@Path("cep") String cep);

}
