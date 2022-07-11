package com.example.sneakify.BackEndGestore;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sneakify.Adapter.AddBrandAdapter;
import com.example.sneakify.Model.Brand;
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

//Classe che permette di aggiugnere un nuovo brand
public class AddBrandFragment extends Fragment {

    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private ArrayList<Brand> brandList;
    private AddBrandAdapter brandAdapter;
    private EditText etTitoloAdd;
    private ImageView ivItemAdd;
    ActivityResultLauncher<String> mGetContent;
    private Uri imageUri;
    private StorageReference storageRef;
    private int numBrand;
    private Button buttonSave;
    public Dialog dialog;

    public AddBrandFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://sneakify-b09a3-default-rtdb.europe-west1.firebasedatabase.app/");
        // viene passata la stringa perchè è localizzato in Belgio e non in USA
        // vengono istanziate qui perchè ci serve farlo soltanto una volta
        myRef = database.getReference(); // istanza del firebase database
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_brand, container, false); // Inflate il layout per il fragment corrente
        setupLayout(view);
        return view;
    }
    public void setupLayout(View view){
        etTitoloAdd= view.findViewById(R.id.etNomeBrand);
        ivItemAdd = view.findViewById(R.id.ivBrandAdd);
        buttonSave = view.findViewById(R.id.bAddBrand);
        mGetContent  = mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), // creazione di una registerForActivityResult per intercettare l'upload della foto
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        imageUri = uri;
                        ivItemAdd.setImageURI(uri); //imposto l'uri dell'immagine
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
                addBrand(); // aggiunge il nuovo brand
            }
        });
        getNumBrand(); //get il numero corrente di brand inseriti nel databse
        getBrandFromDB(view);
    }
    public void getNumBrand(){
        myRef.child("brand").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                numBrand = (int) dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });
    }
    public void getBrandFromDB(View view){ //Prende tutti i brand dal database
        myRef = database.getReference("brand");
        brandList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                brandList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Brand brand = postSnapshot.getValue(Brand.class);
                    brandList.add(brand);
                }
                setupLayoutCategorie(view); //renderizza il layout dei brand
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: ");
            }
        });
    }
    public void setupLayoutCategorie(View v){
        recyclerView = v.findViewById(R.id.rvAddBrand);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL , false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        brandAdapter = new AddBrandAdapter(brandList,getContext());
        recyclerView.setAdapter(brandAdapter);

    }
    public void addBrand(){ // funzione che permette di aggiungere un brand al database
        String title = etTitoloAdd.getText().toString();
        int id = numBrand +1;
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_progess);
        final StorageReference fileRef = storageRef.child(id+"/brandimage"); //memorizza l'immagine
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Brand brandAdd = new Brand(numBrand,title,String.valueOf(uri)); //crea un oggetto di tipo brand con il nome e l'url dell'immagine appena salvata sullo storage
                        myRef.child(String.valueOf(numBrand)).setValue(brandAdd);
                        dialog.dismiss();
                    }
                });
                brandAdapter.notifyDataSetChanged();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                dialog.show(); // inizia la visualizzazione della progress bar
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Ops! Qualcosa è andata male!", Toast.LENGTH_SHORT);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                dialog.dismiss(); // finisce la visualizzazione della progress bar
            }
        });
    }
}