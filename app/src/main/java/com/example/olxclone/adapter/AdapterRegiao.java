package com.example.olxclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxclone.R;

import java.util.List;

public class AdapterRegiao extends RecyclerView.Adapter<AdapterRegiao.MyViewHolder> {

    private final List<String> regioesList;
    private final OnItemClick onItemClick;

    public AdapterRegiao(List<String> regioesList, OnItemClick onItemClick) {
        this.regioesList = regioesList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_regiao, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String regiao = regioesList.get(position);

        holder.textRegiao.setText(regiao);
        holder.itemView.setOnClickListener(v -> onItemClick.itemClick(regiao));
    }

    @Override
    public int getItemCount() {
        return regioesList.size();
    }

    public interface OnItemClick{
        void itemClick(String regiao);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textRegiao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textRegiao = itemView.findViewById(R.id.textRegiao);
        }
    }

}
