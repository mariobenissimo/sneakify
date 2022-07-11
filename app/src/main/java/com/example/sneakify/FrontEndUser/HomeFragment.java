package com.example.sneakify.FrontEndUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sneakify.Adapter.BrandAdapter;
import com.example.sneakify.Model.Item;
import com.example.sneakify.Adapter.ItemAdapter;
import com.example.sneakify.Model.Brand;
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
import java.util.List;


// CLASSE HOME Che visualizza i vari sneakers

// Implementa l'interfaccia della classi adpater in modo tale da poter collegare la classe adapter
// a questa classe e di conseguenza al tap della card view posso gestire il listener in questa classe
public class HomeFragment extends Fragment implements ItemAdapter.itemListener, BrandAdapter.brandListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private List<Integer> itemLike;
    private List<Integer> carello;
    private List<Integer> acquisti;
    private BrandAdapter brandAdapter;
    private ItemAdapter itemAdapter;
    private FirebaseDatabase database;
    private ArrayList<Brand> brandList;
    private ArrayList<Item> itemList;
    private User currentUser;
    private int positionCurrentBrand;
    private TextView tvBrand;
    private EditText etCerca;
    public HomeFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false); // Inflate il layout per il fragment corrente
        setupLayout(view);
        saveUser(view); // controllo se è la prima volta che l'utente entra nell'app in caso positivo lo salvo nel db in caso contrario ripristino lo stato
        return view;
    }

    private void setupLayout(View view) {
        positionCurrentBrand = 0; // posizione iniziale del brand
        tvBrand = view.findViewById(R.id.textViewBrandSelect); // nella fase iniziale viene impostato il testo ad all sneakers per far vedere tutti gli item
        tvBrand.setText("All sneakers");
        etCerca = view.findViewById(R.id.etCerca);
        etCerca.addTextChangedListener(new TextWatcher() {
            // metodi della classe TextWatcher obbligatori da implementare
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getItemFromDB(view,charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void saveUser(View view){
        myRef = database.getReference(); // prendo la reference al database
        user = mAuth.getCurrentUser(); // ottenfo la reference al user corrente
        itemLike = new ArrayList<>(); // inizializzo la lista relativa ai like
        acquisti = new ArrayList<>();
        carello = new ArrayList<>();
        if (user != null) { // se lo user è null significa che è arrivato in questa schermata non passando dal login (IMPOSSIBILE) tuttavia il controllo è d'obbligo
            String email = user.getEmail();
            String uid = user.getUid();
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() { // controllo se esiste un'istanza del database dello users tramite l'uid
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) { // se la chiave esiste ripristino lo user
                        currentUser = snapshot.getValue(User.class);
                        myRef = database.getReference();
                        myRef.child("like").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    int val = ((Long) postSnapshot.getValue()).intValue(); // prendo gli id degli item preferiti dall'utente e li aggiungo alla lista
                                    itemLike.add(val);
                                }
                                getBrandFromDB(view); // a questo punto ottengo i Brand e gli Item dal DB
                                getItemFromDB(view,""); // passo "" come charSequence in quanto non deve essere effettuata nessun tipo di ricerca del tesot
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nel fetch degli item preferiti");
                            }
                        });
                        myRef = database.getReference(); //analogamente a quanto visto prima ripristino la root e prendo la lista degli item acquistati
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
                                System.out.println("Errore nel fetch degli item acquistati");
                            }
                        });
                        myRef = database.getReference(); //analogamente a quanto visto prima ripristino la root e prendo la lista degli item nel carello
                        myRef.child("carello").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    int val = ((Long) postSnapshot.getValue()).intValue();
                                    carello.add(val);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nel fetch degli item nel carello");
                            }
                        });
                    } else {
                        // la chiave non esiste quindi memorizzo l'utente
                        currentUser = new User(email,uid);
                        myRef.child("users").child(uid).setValue(currentUser);
                        getBrandFromDB(view); // a questo punto ottengo i Brand e gli Item dal DB
                        getItemFromDB(view,"");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Errore nel ripristino dello user");
                }
            });
        }
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
                System.out.println("The read failed: ");
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
                System.out.println("Errore nella lettura del database");
            }
        });
    }
    public void setupLayoutCategorie(View v){
        recyclerView = v.findViewById(R.id.rvCategorie); // passo la view in modo tale da agganciare gli elementi grafici nel metodo
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL , false
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        brandAdapter = new BrandAdapter(brandList,getContext(),positionCurrentBrand, this);
        recyclerView.setAdapter(brandAdapter);
    }
    public void setupLayoutItem(View v){
        recyclerView = v.findViewById(R.id.rvItemPref);
        for(int i=0;i<itemList.size();i++){
            System.out.println(itemLike);
            if(itemLike.contains(itemList.get(i).getId())){ // controllo se l'item è nella lista dei preferiti e aggiungo il boolean true
                itemList.get(i).setCuoreLoved(true);
            }else{
                itemList.get(i).setCuoreLoved(false);
            }
        }
        itemAdapter = new ItemAdapter(itemList, getContext(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL , false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onClickItem(int position) { // nel click di un item passo lo user/item all'acitvity Details con l'obiettivo di far visualizzare i dettagli dell'item
        Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
        Item item = itemList.get(position);
        intent.putExtra("item",item);
        intent.putExtra("itemLike", (Serializable) itemLike);  // siccome tutti i dati sono stati ottenuti dal databse vengono passati all'activity in questione in modo tale da non fare una nuova chiamata al db
        intent.putExtra("acquisti", (Serializable) acquisti);
        intent.putExtra("carello", (Serializable) carello);
        intent.putExtra("acquista",0);// parametro per capire da quale parte dell'app arrivo all'activity
        intent.putExtra("user",currentUser);
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

    @Override
    public void onClickBrand(int position) { // il click sul brand cambia la textview e renderizzo il layout in modo tale da avere lo sfondo bianco
        positionCurrentBrand = position;
        tvBrand.setText("Sneakers - " + brandList.get(position).getName());
        setupLayoutCategorie(getView());
        getItemFromDB(getView(),"");
    }
}