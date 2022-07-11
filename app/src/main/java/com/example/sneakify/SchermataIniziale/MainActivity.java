package com.example.sneakify.SchermataIniziale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.sneakify.FrontEndUser.NavigationActivity;
import com.example.sneakify.BackEndGestore.NavigationGestoreActivity;
import com.example.sneakify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Prima activity che viene presentata. Se l'istanza di firebaseAuth è attiva, ovvero esiste ancora l'utente allora sarà indirizzato alla
// home altrimenti sarà indirizzato al login
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // istanza di FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance(); // inizializzo l'istanza del firebase Auth
        setContentView(R.layout.activity_main);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Controlla se esiste un utente
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // indirizza al Login/Register Activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else if(currentUser.getUid().equals("kXsajbqj3uNEV1quDcOofTaW2sv2")){ // Controlla se è lo user admin in tal caso srà indirizzato al frontend admin
            Intent intent = new Intent(this, NavigationGestoreActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, NavigationActivity.class); // se è presente un'istanza di currentUser, lo user è già loggato
            startActivity(intent);                                                     // e sarà indirizzato alla home
            // go to HomePage user
        }
    }
}