package com.example.sneakify.FrontEndUser;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sneakify.Model.Item;
import com.example.sneakify.Model.User;
import com.example.sneakify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Classe che permette la visualizzazione di tutti i dettagli del prodotto
public class ItemDetailsActivity extends AppCompatActivity {

    private Item item;
    private TextView tvDescrizione;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private TextView tvInfo;
    private ImageView ivItem;
    private View vIndietro;
    private View vLove;
    private User currentuser;
    private User newuser;
    private Button buttonGoToCarello;
    private Button buttonAdd;
    private boolean loved;
    private boolean exist;
    private Integer acquista;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<Integer> itemLike;
    private List<Integer> carello;
    private List<Integer> acquisti;
    private RatingBar ratingBar;
    private TextView tvVoto;
    private Button bVota;
    private int numVoto;
    private float sommaVoto;
    private int numVoti;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        item = (Item) getIntent().getSerializableExtra("item"); // ottengo l'item in questione da cui estrapolare le informazioni
        currentuser = (User) getIntent().getSerializableExtra("user"); //ottengo lo user già aggiornate dalle activity precedenti
        acquista = (Integer) getIntent().getSerializableExtra("acquista"); // flag per capire da quale activity provengo
        itemLike = (ArrayList<Integer>) getIntent().getSerializableExtra("itemLike"); // lista degli item preferiti
        acquisti = (ArrayList<Integer>) getIntent().getSerializableExtra("acquisti"); // lista degli item acquistati
        carello = (ArrayList<Integer>) getIntent().getSerializableExtra("carello"); // losta degli item nel carello
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://sneakify-b09a3-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        setupLayoutBase();
    }
    @RequiresApi(api = Build.VERSION_CODES.M) // requeried api = build.version_codes.M per utilizzare il metodo setForeground
    public void setupLayoutBase(){
        buttonAdd = findViewById(R.id.bAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(view); // metodo che in base all'istanza permette di aggiungere l'item o al carello o di acquistarlo
            }
        });
        tvDescrizione = findViewById(R.id.tvDescrizione);
        tvInfo = findViewById(R.id.tvInfo);
        ivItem = findViewById(R.id.ivItemAdd);
        vIndietro = findViewById(R.id.bIndietroModify);
        buttonGoToCarello = findViewById(R.id.bModifyDesign);
        vLove = findViewById(R.id.buttonLove);
        tvVoto = findViewById(R.id.tvRating);
        ratingBar = findViewById(R.id.rbItem); // nel caso in cui ci non ci siano vuoit non posso fare la divisione per 0 ma metto semplicemente la media a 0
        tvVoto.setText(""+item.getRating()); // imposto il rating
        ratingBar.setRating(item.getRating()); // imposto il rating
        bVota = findViewById(R.id.bVota);
        vLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLove(view); // metodo permette di aggiungere elementi alla lista dei preferiti
            }
        });
        bVota.setOnClickListener(new View.OnClickListener() { // button per permettere di votare da 1 a 5 il prodotto
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(ItemDetailsActivity.this); // Viene creato un dialog
                dialog.setContentView(R.layout.dialog_vota);
                Button BAdd = dialog.findViewById(R.id.bAddDialog);
                Button BMinus = dialog.findViewById(R.id.bMinusDialog);
                Button BVota = dialog.findViewById(R.id.bVotaDialog);
                TextView tvVotoDialog = dialog.findViewById(R.id.idVotoDialog);
                numVoto = 0;
                tvVotoDialog.setText(String.valueOf(numVoto));
                BAdd.setOnClickListener(new View.OnClickListener() { //listener per aumentare il voto
                    @Override
                    public void onClick(View view) {
                        if(numVoto < 5){
                            numVoto++;
                            tvVotoDialog.setText(String.valueOf(numVoto));
                        }
                    }
                });
                BMinus.setOnClickListener(new View.OnClickListener() { //listener per diminuire il voto
                    @Override
                    public void onClick(View view) {
                        if(numVoto > 0){
                            numVoto--;
                            tvVotoDialog.setText(String.valueOf(numVoto));
                        }
                    }
                });
                BVota.setOnClickListener(new View.OnClickListener() { //listener per confermare il voto
                    @Override
                    public void onClick(View view) {
                        HashMap<String, Integer> voto = new HashMap<>(); // hashmap utilizzata per caricare il voto
                        myRef = database.getReference("rating");
                        voto.put("id",item.getId());
                        voto.put("voto", numVoto);
                        myRef.child(currentuser.getId()+"*"+item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){ // se lo snapshot esiste signfica che già questo item è stato votato
                                    Toast.makeText(getApplicationContext(),"Hai già votato questo item!",Toast.LENGTH_SHORT).show();
                                }else{
                                    myRef.child(currentuser.getId()+"*"+item.getId()).setValue(voto).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) { // non appena viene fatto l'upload del nuovo voto, calcola la nuova media in modo tale da avere sempre il campo rating aggiornato
                                            myRef = database.getReference("rating");
                                            myRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    sommaVoto = 0;
                                                    numVoti = 0;
                                                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                                        int id = ((Long) postSnapshot.child("id").getValue()).intValue();
                                                        int voto = ((Long) postSnapshot.child("voto").getValue()).intValue();
                                                        if(id == item.getId()){
                                                            numVoti++;
                                                            sommaVoto = sommaVoto +voto;
                                                        }
                                                    }
                                                    myRef = database.getReference("sneakers");
                                                    float media =  sommaVoto/numVoti;
                                                    item.setRating(media);
                                                    System.out.println("media "+media);
                                                    myRef.child(String.valueOf(item.getId())).setValue(item); // effettua l'update del nuovo valore
                                                    ratingBar.setRating(media);
                                                    tvVoto.setText(String.valueOf(media));
                                                    dialog.dismiss(); // una volta che la computazion finisce può essere dismesso il dialog
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    System.out.println("Errore nella lettura dal database");
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Errore nella lettura dal database");
                            }
                        });

                    }
                });
                dialog.show();
            }
        });
        loved = false; // di default il cuore è a false
        setupLayoutItem();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)  // requeried api = build.version_codes.M per utilizzare il metodo setForeground
    private void setupLayoutItem() {
        tvDescrizione.setText(item.getText());
        tvInfo.setText(item.getTitle() + "\nprezzo: "+item.getPrezzo()+"$");
        Glide.with(getBaseContext()).load(item.getImg()).into(ivItem);
        if (itemLike.contains(item.getId())) {
            vLove.setForeground(getResources().getDrawable(R.drawable.ic_cuorepieno));
            loved = true; // se l'item si trova nei listi dei preferti metto loved a true in modo tale da far comparire l'icona
        }
        if(!acquisti.contains(item.getId())) { // se non è già comprato
            if(carello.contains(Integer.valueOf(item.getId()))) { // se non è già comprato e si trova nel carello
                buttonAdd.setText("Rimuovi dal carello");
                exist = true; //true significa che devo eliminare
                buttonGoToCarello.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.rossoPrincipale));
                buttonGoToCarello.setText("Rimosso dal carello");
            }else{ // se non è già comprato e non si trova nel carello
                buttonAdd.setText("Aggiungi al carello");
                exist = false;
                buttonGoToCarello.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.greenPrincipale));
                buttonGoToCarello.setText("Aggiunto al carello");
            }
            if(carello.contains(item.getId()) && acquista == 1){ // se è nel carello non è acquistato e proviene dalla schermata del carello
                buttonAdd.setText("Acquista");
                buttonGoToCarello.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.greenPrincipale));
                buttonGoToCarello.setText("Acquistato");
            }
        }else if(acquisti.contains(item.getId())){ // già acquistato e faccio comparire il button per votare
            buttonAdd.setText("Acquistato");
            bVota.setVisibility(View.VISIBLE);
        }
    }
    public void goBack(View view){ // torno al fragemnt precedente
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void changeLove(View view){
        if(loved == true){
            //togliere l'item dai preferiti
            itemLike.remove(Integer.valueOf(item.getId()));
            myRef = database.getReference("like");
            myRef.child(currentuser.getId()).setValue(itemLike);
            loved = false;
            vLove.setForeground(getResources().getDrawable( R.drawable.ic_cuorevuoto )); // cambia l'icona nella card view
        }else{
            myRef = database.getReference("like");
            itemLike.add(item.getId()); // aggiunge l'item dalla lista dei preferiti
            loved = true;
            myRef.child(currentuser.getId()).setValue(itemLike);
            vLove.setForeground(getResources().getDrawable( R.drawable.ic_cuorepieno ));
        }
    }

    public void addItem(View view){
        String uid = currentuser.getId();
        if(acquisti.contains(item.getId())){ // ho già acquistato questo item
            Toast.makeText(getBaseContext(), "Hai già acquistato questo item", Toast.LENGTH_SHORT).show();
        }else if(!acquisti.contains(item.getId()) && carello.contains(item.getId()) && acquista == 1){ // sto comprando l'item
            // processo di acquisto
            buttonGoToCarello.setVisibility(View.VISIBLE); //rendo il button design visibile per tre secondi
            buttonGoToCarello.setClickable(false);
            buttonAdd.setClickable(false);
            invisibleButton(buttonGoToCarello);
            carello.remove(Integer.valueOf(item.getId())); // rimuovo dalla lista l'item e lo aggiungo nella lista degli acquisti
            acquisti.add(Integer.valueOf(item.getId()));
            //update sia di carello che di acquisti
            myRef = database.getReference("carello");
            myRef.child(uid).setValue(carello);
            myRef = database.getReference("acquisti");
            myRef.child(uid).setValue(acquisti);
             //update le nuove modifiche
        }else{
            if(exist){
                //devo eliminare l'item dal carello
                carello.remove(Integer.valueOf(item.getId()));
            }else{
                //aggiungo al carello
                carello.add(item.getId());
            }
            buttonGoToCarello.setVisibility(View.VISIBLE);
            buttonGoToCarello.setClickable(false);
            buttonAdd.setClickable(false);
            invisibleButton(buttonGoToCarello);
            //update dei nuovi dati
            myRef = database.getReference("carello");
            myRef.child(uid).setValue(carello);
        }
    }

    private void invisibleButton(final View view){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.INVISIBLE);
                buttonGoToCarello.setClickable(true);
                buttonAdd.setClickable(true);
                if (!buttonAdd.getText().toString().equals("Acquista")){
                    if (exist) {
                        buttonAdd.setText("Aggiungi al carello");
                        buttonGoToCarello.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.greenPrincipale));
                        buttonGoToCarello.setText("Aggiunto al carello");
                        exist = false;
                    } else {
                        buttonAdd.setText("Rimuovi dal carello");
                        buttonGoToCarello.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.rossoPrincipale));
                        buttonGoToCarello.setText("Rimosso dal carello");
                        exist = true;
                    }
            }else{
                    buttonAdd.setText("Acquistato");
                }
            }
        }, 1000 * 3);

    }
}
