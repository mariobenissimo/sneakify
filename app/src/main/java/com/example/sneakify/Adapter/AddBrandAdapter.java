package com.example.sneakify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sneakify.Model.Brand;
import com.example.sneakify.R;

import java.util.ArrayList;

public class AddBrandAdapter extends RecyclerView.Adapter<AddBrandAdapter.ViewHolder> {

    ArrayList<Brand> brandModels;
    Context context;

    public AddBrandAdapter(ArrayList<Brand> brandModels, Context context){
        this.brandModels = brandModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_brand,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddBrandAdapter.ViewHolder holder, int position) {
        holder.textView.setText(brandModels.get(position).getName());
        Glide.with(context).load(brandModels.get(position).getImgUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return brandModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivBrandAdd);
            textView = itemView.findViewById(R.id.tvNameBrand);
        }
    }
}
