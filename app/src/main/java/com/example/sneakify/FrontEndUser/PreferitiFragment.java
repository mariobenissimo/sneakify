package com.example.sneakify.FrontEndUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sneakify.Adapter.ItemAdapter;
import com.example.sneakify.Model.Item;
import com.example.sneakify.Model.User;
import com.example.sneakify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Classe che visualizza la lista dei preferiti

public class PreferitiFragment extends Fragment implements  ItemAdapter.itemListener{

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private User currentUser;
    private FirebaseUser user;
    private ArrayList<Item> itemList;
    private List<Integer> itemLike;
    private ArrayList<Integer> carello;
    private ArrayList<Integer> acquisti;

    public PreferitiFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance(); // istanza del firebase database degli utenti
        database = FirebaseDatabase.getInstance("https://sneakify-b09a3-default-rtdb.europe-west1.firebasedatabase.app/"); //istanza del database realtime remoto
        // viene passata la stringa perchè è localizzato in Belgio e non in USA
        // vengono istanziate qui perchè ci serve farlo soltanto una volta
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferiti, container, false); // Inflate il layout per il fragment corrente
        getUserdata(view); // prendo lo stato dell'utente
        return view;
    }
    public void getUserdata(View view){
        myRef = database.getReference(); // prendo la reference al database
        user = mAuth.getCurrentUser(); // ottenfo la reference al user corrente
        itemLike = new ArrayList<>();
        carello = new ArrayList<>();
        acquisti = new ArrayList<>();
        if (user != null) { // Ovviamente lo user sarà diverso da null in quanto in questa activity può essere raggiunta solo dopo la home tuttavia firebase suggerisce di fare sempre questo controllo per motivi di sicurezza
            String uid = user.getUid();
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        currentUser = snapshot.getValue(User.class);
                        myRef.child("carello").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() { // fetch dei dati del carello
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    int val = ((Long) postSnapshot.getValue()).intValue();
                                    carello.add(val);
                                }
                                getItemFromDB(view);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nella lettura del database dei dati del carello");
                            }
                        });
                        myRef.child("acquisti").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    int val = ((Long) postSnapshot.getValue()).intValue();
                                    acquisti.add(val);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nella lettura del database dei dati acquistati");
                            }
                        });
                        myRef.child("like").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    int val = ((Long) postSnapshot.getValue()).intValue();
                                    itemLike.add(val);
                                }
                                getItemFromDB(view);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nella lettura del database dei dati della lista dei preferiti");
                            }
                        });
                    } else {
                        System.out.println("Errore nella lettura dello user");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Errore nella lettura dal databse");
                }
            });
        }
    }
    public void getItemFromDB(View view){
        myRef = database.getReference("sneakers"); // path to sneakers
        itemList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    if(itemLike.contains(item.getId())){ // se l'item si trova nella lista dei desiderati allora può essere inserito nella lista
                        itemList.add(item);
                    }
                }
                setupLayoutItem(view);  // posso renderizzare il layout degli item
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Errore nella lettura del database");
            }
        });
    }
    public void setupLayoutItem(View v){
        recyclerView = v.findViewById(R.id.rvItemPref);
        for(int i=0;i<itemList.size();i++){
            itemList.get(i).setCuoreLoved(true); // tutti gli item sono nella lista dei like quindi posso mettere il cuore boolean a true
        }
        itemAdapter = new ItemAdapter(itemList, getContext(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL , false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onClickItem(int position) { // nel click di un item passo lo user/item all'activity Details con l'obiettivo di far visualizzare i dettagli dell'item
        Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
        Item item = itemList.get(position);
        intent.putExtra("item",item);
        intent.putExtra("itemLike", (Serializable) itemLike);
        intent.putExtra("acquisti", (Serializable) acquisti);
        intent.putExtra("carello", (Serializable) carello);
        intent.putExtra("user", currentUser);
        intent.putExtra("acquista",0); // parametro per capire da quale parte dell'app arrivo all'activity
        startActivity(intent);
    }

    @Override
    public void onClickCuore(int position) { // modifica dei like/unlike degli item
        if(itemList.get(position).isCuoreLoved()){
            myRef = database.getReference("like");
            itemList.get(position).setCuoreLoved(false); // modifico il boolean
            itemLike.remove(Integer.valueOf(itemList.get(position).getId()));
            myRef.child(user.getUid()).setValue(itemLike);
        }else {
            itemList.get(position).setCuoreLoved(true); // modifico il boolean
            myRef = database.getReference("like");
            itemLike.add(itemList.get(position).getId());
            myRef.child(user.getUid()).setValue(itemLike); // salvo le nuovo modifiche
        }
        itemAdapter.notifyItemChanged(position); // aggiorno le modifiche grafiche
    }
}