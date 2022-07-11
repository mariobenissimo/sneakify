package com.example.sneakify.FrontEndUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sneakify.Model.Item;
import com.example.sneakify.Adapter.ItemCarelloAdapter;
import com.example.sneakify.R;
import com.example.sneakify.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

// Implementa l'interfaccia della classe Itemcarello adpater in modo tale da poter collegare la classe adapter
// a questa classe e di conseguenza al tap della card view posso gestire il listener in questa classe
public class CarelloFragment extends Fragment implements ItemCarelloAdapter.itemListener {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private ItemCarelloAdapter itemCarelloAdapter;
    private ItemCarelloAdapter itemAcquistiAdapter;
    private User newuser;
    private FirebaseUser user;
    private ArrayList<Item> itemList;
    private ArrayList<Item> itemListAcquisti;
    private ArrayList<Integer> carello;
    private ArrayList<Integer> acquisti;
    private ArrayList<Integer> itemLike;
    public CarelloFragment() {
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_carello, container, false);
        getUserdata(view); // prendo lo stato dell'utente
        return view;
    }

    public void getUserdata(View view){
        myRef = database.getReference(); // prendo la reference al database
        user = mAuth.getCurrentUser(); // ottenfo la reference al user corrente
        itemLike = new ArrayList<>();
        carello = new ArrayList<>();
        acquisti = new ArrayList<>();
        if (user != null) {
            String uid = user.getUid();
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        //ripristino le informazioni dell'utente
                        newuser = snapshot.getValue(User.class);
                        myRef.child("carello").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    int val = ((Long) postSnapshot.getValue()).intValue();
                                    carello.add(val);
                                }
                                getItemFromDB(view); // renderizzare il layout degli item del carello
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nella lettura della lista del carello");
                            }
                        });
                        myRef.child("acquisti").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    int val = ((Long) postSnapshot.getValue()).intValue();
                                    acquisti.add(val);
                                }
                                getAcquistiFromDB(view);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nella lettura della lista degli acquisti");
                            }
                        });
                        myRef.child("like").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    int val = ((Long) postSnapshot.getValue()).intValue();
                                    itemLike.add(val);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nella lettura della lista dei preferiti");
                            }
                        });
                    } else {
                        System.out.println("Errore nella lettura dello user dal database");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Errore nella lettura del database");
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
                    if(carello.contains(item.getId())){ // se l'iteme si trova nella lista del carello allora può essere inserito nella lista
                        itemList.add(item);
                    }
                }
                setupLayoutItem(view); // posso reanderizzare il layout del carello
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Errore nella lettura del database");
            }
        });
    }

    private void setupLayoutItem(View view) {
        recyclerView = view.findViewById(R.id.rvItemCarello);
        itemCarelloAdapter = new ItemCarelloAdapter(itemList, getContext(),this,0); // viene inseriro un flah nell'adapter in modo tale da capire se l'item è nel carello o negli acquisti 0 = carello 1= acquisti
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL , false
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemCarelloAdapter);
    }

    public void getAcquistiFromDB(View view){
        myRef = database.getReference("sneakers"); // path to sneakers
        itemListAcquisti = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itemListAcquisti.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    if(acquisti.contains(item.getId())){ // se l'item si trova nella lista degli acquisti allora può essere inserito nella lista
                        itemListAcquisti.add(item);
                    }
                }
                setupLayoutItemAcquisti(view); // posso reanderizzare il layout degli item
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Errore nella lettura del database");
            }
        });
    }

    public void setupLayoutItemAcquisti(View view) {
        recyclerView = view.findViewById(R.id.rvItemAcquisti);
        itemAcquistiAdapter = new ItemCarelloAdapter(itemListAcquisti, getContext(),this,1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL , false
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemAcquistiAdapter);
    }

    @Override
    public void onClickItem(int position, int flag) {
        if(flag == 0 ){ // se il flag è 0 vuol dire che è nel carello
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            Item item = itemList.get(position);
            intent.putExtra("item",item);
            intent.putExtra("user",newuser);
            intent.putExtra("itemLike", (Serializable) itemLike);
            intent.putExtra("acquisti", (Serializable) acquisti);
            intent.putExtra("carello", (Serializable) carello);
            intent.putExtra("acquista", 1); // lo voglio acquistare
            startActivity(intent);
        }else{ //altrimenti è negli acquisti
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            Item item = itemListAcquisti.get(position);
            intent.putExtra("item",item);
            intent.putExtra("user",newuser);
            intent.putExtra("itemLike", (Serializable) itemLike);
            intent.putExtra("acquisti", (Serializable) acquisti);
            intent.putExtra("carello", (Serializable) carello);
            intent.putExtra("acquista", 2); // già acquistato
            startActivity(intent);
        }
    }
}