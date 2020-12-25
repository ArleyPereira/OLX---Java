package com.example.olxclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxclone.R;
import com.example.olxclone.model.Categoria;

import java.util.List;

public class AdapterCategoria extends RecyclerView.Adapter<AdapterCategoria.MyViewHolder> {

    private final List<Categoria> categoriaList;
    private final OnItemClick onItemClick;

    public AdapterCategoria(List<Categoria> categoriaList, OnItemClick onItemClick) {
        this.categoriaList = categoriaList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_categoria, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Categoria categoria = categoriaList.get(position);

        holder.textCategoria.setText(categoria.getNome());
        holder.imgCategoria.setImageResource(categoria.getImagem());

        holder.itemView.setOnClickListener(v -> onItemClick.itemClick(categoria));

    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    public interface OnItemClick{
        void itemClick(Categoria categoria);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imgCategoria;
        TextView textCategoria;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoria = itemView.findViewById(R.id.textCategoria);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
        }
    }

}
