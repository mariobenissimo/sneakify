package com.example.sneakify.BackEndGestore;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sneakify.FrontEndUser.NavigationActivity;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//Classe che permette di modificare il prodotto
public class ModifyItemGestore extends AppCompatActivity {

    private Item item;
    private EditText etDescrizioneModify;
    private EditText etPrezzoModify;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private EditText etTitoloModify;
    private ImageView ivItemModify;
    private View vIndietro;
    private Button buttonSave;
    private Button buttonCancella;
    ActivityResultLauncher<String> mGetContent;
    private Uri imageUri;
    public Dialog dialog;
    Spinner spinner;
    private ArrayList<Brand> brandList;
    private StorageReference storageRef;
    private int fkBrand;
    private List<Long> itemLike;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_item_gestore);
        item = (Item) getIntent().getSerializableExtra("item");
        storageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance("https://sneakify-b09a3-default-rtdb.europe-west1.firebasedatabase.app/");
        setupLayout();
    }


    private void setupLayout() {
        etDescrizioneModify = findViewById(R.id.etDescrizioneAdd);
        etTitoloModify= findViewById(R.id.etTitleAdd);
        etPrezzoModify = findViewById(R.id.etPrezzoAdd);
        ivItemModify = findViewById(R.id.ivItemAdd);
        vIndietro = findViewById(R.id.bIndietroModify);
        buttonSave = findViewById(R.id.bAdd);
        etDescrizioneModify.setText(item.getText());
        etTitoloModify.setText(item.getTitle());
        etPrezzoModify.setText(item.getPrezzo());
        buttonCancella = findViewById(R.id.bCancellaItem);

        buttonCancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef = database.getReference();
                 myRef.child("sneakers").child(String.valueOf(item.getId())).removeValue();
                 // dopo aver cancellato il relativo sneakers vengono cancellati tutti i record degli utenti con quell'id
                myRef = database.getReference("like");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            itemLike = new ArrayList<>();
                            String idUtente = postSnapshot.getKey();
                            itemLike = (ArrayList<Long>) postSnapshot.getValue();
                            itemLike.remove(Long.valueOf(String.valueOf(item.getId())));
                            myRef = database.getReference("like");
                            myRef.child(idUtente).setValue(itemLike);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                myRef = database.getReference("acquisti");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            itemLike = new ArrayList<>();
                            String idUtente = postSnapshot.getKey();
                            itemLike = (ArrayList<Long>) postSnapshot.getValue();
                            itemLike.remove(Long.valueOf(String.valueOf(item.getId())));
                            myRef = database.getReference("acquisti");
                            myRef.child(idUtente).setValue(itemLike);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                myRef = database.getReference("carello");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            itemLike = new ArrayList<>();
                            String idUtente = postSnapshot.getKey();
                            itemLike = (ArrayList<Long>) postSnapshot.getValue();
                            itemLike.remove(Long.valueOf(String.valueOf(item.getId())));
                            myRef = database.getReference("carello");
                            myRef.child(idUtente).setValue(itemLike);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                myRef = database.getReference("rating");
                myRef.orderByChild("id").equalTo(item.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            postSnapshot.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: ");
                    }
                });
                finish();
            }
        });
        Glide.with(getBaseContext()).load(item.getImg()).into(ivItemModify);
        buttonSave.setText("Salva le modifiche");
        spinner = findViewById(R.id.spBrandModify);
        mGetContent  = mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), //set an register for activity result to get uri from gallery intent
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        imageUri = uri;
                        ivItemModify.setImageURI(uri);
                    }
                });
        ivItemModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*"); // open gallery
            }
        });
        getBrandFromDB();
    }
    public void getBrandFromDB(){ // funzione che permette di prendere i brand in modo tale da aggiungerli allo spinner
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
                setupLayoutSpinner(); // a questo punto si può riempire lo spinner
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: ");
            }
        });
    }
    public void setupLayoutSpinner(){
        ArrayAdapter<Brand> adapter =
                new ArrayAdapter<Brand>(ModifyItemGestore.this,  android.R.layout.simple_spinner_dropdown_item, brandList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);
        int idSelection = 0;
        for(int i=0;i<brandList.size();i++){
            if(brandList.get(i).getId() == item.getBrandId()){
                idSelection = i;
            }
        }
        spinner.setSelection(idSelection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fkBrand = brandList.get(i).getId(); // l'item selzionato sarà l'fk dell'item che stiamo provando ad aggiornare
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void goBackModify(View view){
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }

    public void addItemModify(View view){
        myRef = database.getReference("sneakers");
        String title = etTitoloModify.getText().toString();
        String prezzo = etPrezzoModify.getText().toString();
        String descrizione = etDescrizioneModify.getText().toString();
        item.setText(descrizione);
        item.setTitle(title);
        item.setPrezzo(prezzo);
        item.setBrandId(fkBrand);
        if(imageUri != null){ // devo fare l'upload dell'immagine
            dialog = new Dialog(ModifyItemGestore.this);
            dialog.setContentView(R.layout.dialog_progess);
            final StorageReference fileRef = storageRef.child(item.getId()+"/image"); // l'immagine sarà inserita nel path idItem/image
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //uri è la locazione dell'immagine nello storage
                            item.setImg(String.valueOf(uri));
                            myRef.child(String.valueOf(item.getId())).setValue(item);
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
                    Toast.makeText(ModifyItemGestore.this,"Ops! Qualcosa è andata male!", Toast.LENGTH_SHORT);
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    dialog.dismiss();
                }
            });
        }else{
            myRef.child(String.valueOf(item.getId())).setValue(item);
            Toast.makeText(ModifyItemGestore.this,"Modifiche salvate!", Toast.LENGTH_SHORT).show();
        }
    }

}