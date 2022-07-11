package com.example.sneakify.BackEndGestore;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sneakify.FrontEndUser.ItemDetailsActivity;
import com.example.sneakify.Model.Brand;
import com.example.sneakify.Model.Item;
import com.example.sneakify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

//Classe che permette di aggiugnere un nuovo prodotto

public class AddItemFragment extends Fragment {

    private Item item;
    private EditText etDescrizioneAdd;
    private EditText etPrezzoAdd;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private EditText etTitoloAdd;
    private ImageView ivItemAdd;
    private Button buttonSave;
    private int numItem;
    private int fkBrand;
    ActivityResultLauncher<String> mGetContent;
    private Uri imageUri;
    private StorageReference storageRef;
    Spinner spinner;
    private ArrayList<Brand> brandList;
    public Dialog dialog;

    public AddItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        storageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance("https://sneakify-b09a3-default-rtdb.europe-west1.firebasedatabase.app/");
        // viene passata la stringa perchè è localizzato in Belgio e non in USA
        // vengono istanziate qui perchè ci serve farlo soltanto una volta
        myRef = database.getReference("sneakers");  // istanza del firebase database
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_item, container, false);
        setupLayout(view); //setup del layout
        getBrandFromDB(view); // fetch dei brand dal database per inizializzare lo spinner
        return view;
    }
    public void setupLayout(View view){
        etDescrizioneAdd = view.findViewById(R.id.etDescrizioneAdd);
        etTitoloAdd= view.findViewById(R.id.etTitleAdd);
        etPrezzoAdd = view.findViewById(R.id.etPrezzoAdd);
        ivItemAdd = view.findViewById(R.id.ivItemAdd);
        buttonSave = view.findViewById(R.id.bAdd);
        spinner = view.findViewById(R.id.spBrand);
        myRef = database.getReference("sneakers");
        myRef.addValueEventListener(new ValueEventListener() { // ottenere il progressivo degli sneakers
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    numItem = item.getId();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });
        mGetContent  = mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), //set an register for activity result to get uri from gallery intent
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        imageUri = uri; //imposto l'uri dell'immagine
                        ivItemAdd.setImageURI(uri);
                    }
                });
        ivItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*"); //Apre l'intent
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(view); // aggiungere item
            }
        });
    }
    public void getBrandFromDB(View view){ // funzione che permette di prendere i brand in modo tale da aggiungerli allo spinner
        myRef = database.getReference("brand");
        brandList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                brandList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Brand brand = postSnapshot.getValue(Brand.class);
                    if(brand.getId() != 0){ // il primo è "all sneakers" non verrà inserito
                        brandList.add(brand);
                    }
                }
                setupLayoutSpinner(view); // a questo punto si può riempire lo spinner
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: ");
            }
        });
    }

    public void setupLayoutSpinner(View view){
        ArrayAdapter<Brand> adapter =
                new ArrayAdapter<Brand>(getContext(),  android.R.layout.simple_spinner_dropdown_item, brandList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fkBrand = brandList.get(i).getId(); // l'item selzionato sarà l'fk dell'item che stiamo provando ad inserire
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void addItem(View view) { // funzione che permette di aggiungere il nuovo item
        String title = etTitoloAdd.getText().toString();
        String prezzo = etPrezzoAdd.getText().toString();
        String descrizione = etDescrizioneAdd.getText().toString();
        myRef = database.getReference("sneakers"); // path per inserire lo sneakers
        int id = numItem +1; // l'id del nuovo item
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_progess);
        final StorageReference fileRef = storageRef.child(id+"/image"); // l'iimagine sarà inserita nel path idItem/image
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //uri è la locazione dell'immagine nello storage
                        item = new Item(id,title,descrizione,prezzo,false,String.valueOf(uri), fkBrand, 0, 0);
                        myRef.child(String.valueOf(id)).setValue(item);
                        dialog.dismiss();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                dialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Ops! Qualcosa è andata male!", Toast.LENGTH_SHORT);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                dialog.dismiss();
            }
        });
    }
}