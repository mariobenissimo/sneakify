package com.example.sneakify.Model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.io.Serializable;

// Class Item viene implementata l'interfaccia Serializable in modo tale da poter essere passata come oggetto alle diverse activity
public class Item implements Serializable {
    private String img;
    private int id;
    private String title;
    private String text;
    private String prezzo;
    private boolean cuoreLoved;
    private int brandId;
    private float rating;
    private int numVoti;

    public Item() { } // costruttore vuoto per essere utilizzato da firebase nel pull dei dati da firebase

    public Item(int id,String title, String text, String prezzo, boolean cuoreLoved, String img, int brandId, float rating, int numVoti) {
        this.title = title;
        this.text = text;
        this.prezzo = prezzo;
        this.cuoreLoved = cuoreLoved;
        this.id = id;
        this.img = img;
        this.brandId = brandId;
        this.rating = rating;
        this.numVoti = numVoti;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(String prezzo) {
        this.prezzo = prezzo;
    }

    public boolean isCuoreLoved() {
        return cuoreLoved;
    }

    public void setCuoreLoved(boolean cuoreLoved) {
        this.cuoreLoved = cuoreLoved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getNumVoti() {
        return numVoti;
    }

    public void setNumVoti(int numVoti) {
        this.numVoti = numVoti;
    }

    @Override
    public String toString() {
        return "Item{" +
                "img='" + img + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", prezzo='" + prezzo + '\'' +
                ", cuoreLoved=" + cuoreLoved +
                ", brandId=" + brandId +
                ", rating=" + rating +
                ", numVoti=" + numVoti +
                '}';
    }
}
