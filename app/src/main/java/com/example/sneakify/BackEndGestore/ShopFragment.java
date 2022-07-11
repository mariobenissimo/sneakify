package com.example.sneakify.BackEndGestore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sneakify.Adapter.BrandAdapter;
import com.example.sneakify.Adapter.ItemCarelloAdapter;
import com.example.sneakify.Model.Brand;
import com.example.sneakify.Model.Item;
import com.example.sneakify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopFragment extends Fragment implements ItemCarelloAdapter.itemListener, BrandAdapter.brandListener{

    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private BrandAdapter brandAdapter;
    private FirebaseDatabase database;
    private ArrayList<Brand> brandList;
    private ArrayList<Item> itemList;
    private ItemCarelloAdapter itemCarelloAdapter;
    private int positionCurrentBrand;
    private TextView textViewBrand;
    private EditText editTextCerca;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://sneakify-b09a3-default-rtdb.europe-west1.firebasedatabase.app/"); //istanza del database realtime remoto
        // viene passata la stringa perchè è localizzato in Belgio e non in USA
        // vengono istanziate qui perchè ci serve farlo soltanto una volta
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_shop, container, false); // Inflate il layout per il fragment corrente
        positionCurrentBrand = 0; // posizione iniziale del brand
        textViewBrand = view.findViewById(R.id.textViewBrandSelectGestore); // nella fase iniziale viene impostato il testo ad all sneakers per far vedere tutti gli item
        textViewBrand.setText("All sneakers");
        editTextCerca = view.findViewById(R.id.editTextCercaGestore);
        editTextCerca.addTextChangedListener(new TextWatcher() {
            // metodi della classe TextWatcher obbligatori da implementare
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(charSequence);
                getItemFromDB(view,charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getBrandFromDB(view); // ottengo i Brand e gli Item dal DB
        getItemFromDB(view, "");
        return view;
    }
    public void getBrandFromDB(View view){
        myRef = database.getReference("brand"); // path to brand
        brandList = new ArrayList<>(); // lista dei brand
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                brandList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Brand brand = postSnapshot.getValue(Brand.class);
                    brandList.add(brand);
                }
                setupLayoutCategorie(view); // Una volta che i brand sono stati pullati dal db si può formare il database
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Errore nella lettura del databse");
            }
        });
    }
    public void getItemFromDB(View view, CharSequence charSequence){
        myRef = database.getReference("sneakers"); // path to sneakers
        itemList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    if(charSequence.length() == 0){ // se la lunghezza della sequenza dei caratteri dalla barra di ricerca è nulla posso procedere a verificare soltato se è stato cliccato un brand
                        if(positionCurrentBrand !=0 && positionCurrentBrand == item.getBrandId()){  // se ho cliccato un brand posso aggiungere gli item di quel brand alla lista
                            itemList.add(item);
                        }else if(positionCurrentBrand ==0){ // se non ho cliccato nessun brand posso aggiungere tutti gli item alla lista
                            itemList.add(item);
                        }
                    }else{
                        if(positionCurrentBrand !=0 && positionCurrentBrand == item.getBrandId() && item.getTitle().contains(charSequence)){  // se ho cliccato un brand posso aggiungere gli item di quel brand alla lista se e solo se il titolo contiene la char sequence della barra di ricerca
                            itemList.add(item);
                        }else if(positionCurrentBrand ==0  && item.getTitle().contains(charSequence)) { // se non ho cliccato nessun brand posso aggiungere tutti gli item alla lista se e solo se il titolo contiene la char sequence della barra di ricerca
                            itemList.add(item);
                        }
                    }
                }
                setupLayoutItem(view); // posso reanderizzare il layout degli item
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Errore nella lettura del databse");
            }
        });
    }

    private void setupLayoutItem(View view) {
        recyclerView = view.findViewById(R.id.rvItemGestore);
        itemCarelloAdapter = new ItemCarelloAdapter(itemList, getContext(),this, 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL , false
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemCarelloAdapter);
    }

    public void setupLayoutCategorie(View v){
        recyclerView = v.findViewById(R.id.rvCategorieGestore); // passo la view in modo tale da agganciare gli elementi grafici nel metodo
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL , false
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        brandAdapter = new BrandAdapter(brandList,getContext(),positionCurrentBrand, this);
        recyclerView.setAdapter(brandAdapter);
    }

    @Override
    public void onClickItem(int position, int flag) { // click su un item permette di aprire l'acitvity per modificare
        Intent intent = new Intent(getContext(), ModifyItemGestore.class);
        Item item = itemList.get(position);
        intent.putExtra("item", item);
        startActivity(intent);
    }


    @Override
    public void onClickBrand(int position) { // il click sul brand cambia la textview e renderizzo il layout in modo tale da avere lo sfondo bianco
        positionCurrentBrand = position;
        textViewBrand.setText("Sneakers - " + brandList.get(position).getName());
        setupLayoutCategorie(getView());
        getItemFromDB(getView(),"");
    }
}