package com.example.sneakify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sneakify.Model.Item;
import com.example.sneakify.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    ArrayList<Item> itemModels;
    itemListener mitemListener;
    Context context;
    private StorageReference mStorage;
    public ItemAdapter(ArrayList<Item> itemModels, Context context, itemListener mitemListener) {
        this.itemModels = itemModels;
        this.context = context;
        this.mitemListener = mitemListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item,parent,false);
        mStorage = FirebaseStorage.getInstance().getReference();
        return new ViewHolder(view, mitemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(itemModels.get(position).getTitle());
        holder.prezzo.setText(itemModels.get(position).getPrezzo());
        if(itemModels.get(position).isCuoreLoved()){
            holder.ivCuore.setBackground(AppCompatResources.getDrawable(context,R.drawable.ic_cuorepieno)); // metto il background con il cuore pieno
        }else{
            holder.ivCuore.setBackground(AppCompatResources.getDrawable(context,R.drawable.ic_cuorevuoto));
        }
        Glide.with(context).load(itemModels.get(position).getImg()).into(holder.ivItem);

    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }
    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView prezzo;
        ImageView ivItem;
        ImageButton ivCuore;
        itemListener itemlistener;
        public ViewHolder(@NonNull View itemView, itemListener itemlistener) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivBrandAdd);
            title = itemView.findViewById(R.id.tvNameBrand);
            prezzo = itemView.findViewById(R.id.tvPrezzo);
            ivCuore = itemView.findViewById(R.id.ivCuore);
            this.itemlistener = itemlistener;
            ivCuore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemlistener.onClickCuore(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemlistener.onClickItem(getAdapterPosition());
        }

    }

    public interface itemListener{
        void onClickItem(int position);
        void onClickCuore(int position);
    }
}
