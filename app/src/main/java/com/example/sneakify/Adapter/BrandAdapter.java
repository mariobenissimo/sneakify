package com.example.sneakify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sneakify.Model.Brand;
import com.example.sneakify.R;

import java.util.ArrayList;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    ArrayList<Brand> brandModels;
    brandListener mbrandListener;
    Context context;
    int positionItemSelected;

    public BrandAdapter(ArrayList<Brand> brandModels, Context context, int positionItemSelected , brandListener brandListener) {
        this.brandModels = brandModels;
        this.context = context;
        this.positionItemSelected = positionItemSelected;
        this.mbrandListener = brandListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_item,parent,false);
        return new ViewHolder(view, mbrandListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == positionItemSelected){ // nel caso in cui è il brand selezionato metto lo sfondo bianco
            holder.textView.setText(brandModels.get(position).getName());
            holder.linearLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.brand_rounded));
            Glide.with(context).load(brandModels.get(position).getImgUrl()).into(holder.imageView);
        }else{
            holder.textView.setText(brandModels.get(position).getName());
            Glide.with(context).load(brandModels.get(position).getImgUrl()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return brandModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        private LinearLayout linearLayout;
        private brandListener brandListener;
        public ViewHolder(@NonNull View itemView, brandListener brandListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivBrand);
            textView = itemView.findViewById(R.id.tvBrand);
            this.brandListener = brandListener;
            linearLayout = itemView.findViewById(R.id.lvBrand);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    brandListener.onClickBrand(getAdapterPosition());
                }
            });
        }
    }

    public interface brandListener{
        void onClickBrand(int position);
    }
}
