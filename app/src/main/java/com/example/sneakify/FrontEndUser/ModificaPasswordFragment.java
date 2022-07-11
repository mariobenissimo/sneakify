package com.example.sneakify.FrontEndUser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sneakify.Model.User;
import com.example.sneakify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Classe che permette di modificare la password

public class ModificaPasswordFragment extends Fragment {

    private EditText etEmail;
    private EditText etVecchiaPassword;
    private EditText etNuovaPassword;
    private Button bSave;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private User newuser;
    private DatabaseReference myRef;

    public ModificaPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modifica_password, container, false);
        mAuth = FirebaseAuth.getInstance(); // istanza del firebase database degli utenti
        database = FirebaseDatabase.getInstance("https://sneakify-b09a3-default-rtdb.europe-west1.firebasedatabase.app/"); //istanza del database realtime remoto
        // viene passata la stringa perchè è localizzato in Belgio e non in USA
        user = mAuth.getCurrentUser();
        getUserdata(view);// ottengo l' utente
        return view;
    }

    public void getUserdata(View view){
        myRef = database.getReference(); // prendo la reference al database
        user = mAuth.getCurrentUser(); // ottenfo la reference al user corrente
        if (user != null) {
            String uid = user.getUid();
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        //ripristino utente
                        newuser = snapshot.getValue(User.class);
                    } else {
                        System.out.println("Errore nella lettura del database");
                    }
                    // a questo punto renderizzo il layout
                    setupLayout(view);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Errore nella lettura del database");
                }
            });
        }
    }
    public void setupLayout(View view){

        etEmail = view.findViewById(R.id.etEmailPWS);
        etVecchiaPassword = view.findViewById(R.id.etPassLogin);
        etNuovaPassword = view.findViewById(R.id.etNuovaPWS);
        bSave = view.findViewById(R.id.buttonSavePSW);
        etEmail.setText(newuser.getEmail()); // imposto l'email nella editText
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etEmail.getText().toString() == null){
                    Toast.makeText(getContext(), "Attenzione devi inserire l'email", Toast.LENGTH_SHORT).show();
                }else if(etVecchiaPassword.getText().toString() == null || etNuovaPassword.getText().toString() == null){
                    Toast.makeText(getContext(), "Attenzione devi inserire la password", Toast.LENGTH_SHORT).show();
                }
                if(etEmail.getText().toString() != null && etVecchiaPassword.getText().toString() != null && etNuovaPassword.getText().toString() != null){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), etVecchiaPassword.getText().toString()); // creo un oggetto di tipo EmailAuthProvider in modo tale da poter cambiare la password utilizzando la password vecchia e l'email
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(etNuovaPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Password Aggiornata", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Attenzione! Password non aggiornata, controlla i campi.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        System.out.println("Errore nella lettura del database");
                                    }
                                }
                            });
                }
            }
        });
    }
}