package com.example.olxclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxclone.R;
import com.example.olxclone.Util.GetMask;
import com.example.olxclone.model.Anuncio;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAnuncio extends RecyclerView.Adapter<AdapterAnuncio.MyViewHolder> {

    private final List<Anuncio> anuncioList;
    private final OnItemClick onItemClick;
    private final Context context;

    public AdapterAnuncio(List<Anuncio> anuncioList, OnItemClick onItemClick, Context context) {
        this.anuncioList = anuncioList;
        this.onItemClick = onItemClick;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Anuncio anuncio = anuncioList.get(position);

        Picasso.get().load(anuncio.getUrlFotos().get(0))
                .resize(500, 500).centerCrop()
                .into(holder.imagemAnuncio);

        holder.textTitulo.setText(anuncio.getNome());
        holder.textPreco.setText(context.getString(R.string.valor, GetMask.getValor(anuncio.getPreco())));
        holder.textData.setText(context.getString(R.string.data_publicacao_bairro, GetMask.getDate(anuncio.getDataCadastro(), 4), anuncio.getLocal().getBairro()));

        holder.itemView.setOnClickListener(v -> onItemClick.itemClick(anuncio));

    }

    @Override
    public int getItemCount() {
        return anuncioList.size();
    }

    public interface OnItemClick {
        void itemClick(Anuncio anuncio);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemAnuncio;
        TextView textTitulo, textPreco, textData;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemAnuncio = itemView.findViewById(R.id.imagemAnuncio);
            textTitulo = itemView.findViewById(R.id.textTitulo);
            textPreco = itemView.findViewById(R.id.textPreco);
            textData = itemView.findViewById(R.id.textData);
        }
    }

}
