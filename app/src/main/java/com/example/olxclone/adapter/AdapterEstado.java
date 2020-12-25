package com.example.olxclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxclone.R;
import com.example.olxclone.model.Estado;

import java.util.List;

public class AdapterEstado extends RecyclerView.Adapter<AdapterEstado.MyViewHolder> {

    private final List<Estado> estadoList;
    private final OnItemClick onItemClick;

    public AdapterEstado(List<Estado> estadoList, OnItemClick onItemClick) {
        this.estadoList = estadoList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_estado, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Estado estado = estadoList.get(position);

        holder.textEstado.setText(estado.getNome());
        holder.itemView.setOnClickListener(v -> onItemClick.itemClick(estado));
    }

    @Override
    public int getItemCount() {
        return estadoList.size();
    }

    public interface OnItemClick{
        void itemClick(Estado estado);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textEstado;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textEstado = itemView.findViewById(R.id.textRegiao);
        }
    }

}
