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
import com.example.sneakify.Model.Item;
import com.example.sneakify.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ItemCarelloAdapter extends RecyclerView.Adapter<ItemCarelloAdapter.ViewHolder> {

    ArrayList<Item> itemCarello;
    Context context;
    itemListener mitemListener;
    private StorageReference mStorage;
    int flag;

    public ItemCarelloAdapter(ArrayList<Item> itemCarello, Context context, itemListener mitemListener, int flag) {
        this.itemCarello = itemCarello;
        this.context = context;
        this.mitemListener = mitemListener;
        this.flag = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_item_carello,parent,false);
        return new ViewHolder(view, mitemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvItemCarello.setText(itemCarello.get(position).getTitle());
        holder.tvPrezzoCarello.setText("Prezzo: " + itemCarello.get(position).getPrezzo());
        Glide.with(context).load(itemCarello.get(position).getImg()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemCarello.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView tvItemCarello;
        private TextView tvPrezzoCarello;
        itemListener mitemListener;
        public ViewHolder(@NonNull View itemView, itemListener mitemListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivItemCarello);
            tvItemCarello = itemView.findViewById(R.id.tvItemCarello);
            tvPrezzoCarello = itemView.findViewById(R.id.tvPrezzoCarello);
            this.mitemListener = mitemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mitemListener.onClickItem(getAdapterPosition(),flag);
        }
    }

    public interface itemListener{
        void onClickItem(int position, int flag);
    }
}
