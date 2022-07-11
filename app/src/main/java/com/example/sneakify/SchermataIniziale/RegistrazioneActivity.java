package com.example.sneakify.SchermataIniziale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sneakify.FrontEndUser.NavigationActivity;
import com.example.sneakify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
// classe che permette di registrare un nuovo utente
public class RegistrazioneActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail;
    private EditText etPassowrd;
    private Button bRegistrati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);
        mAuth = FirebaseAuth.getInstance();
        setupLayout(); // funzione utilizzata per inizializzare il layout grafico
    }
    void setupLayout(){
        etEmail = (EditText) findViewById(R.id.etEmailReg);
        etPassowrd = (EditText) findViewById(R.id.etPasswordReg);
        bRegistrati = (Button) findViewById(R.id.bRegistrati);
    }
    public void SignUp(View v){
        String email = etEmail.getText().toString();
        String password = etPassowrd.getText().toString();
        singUpWithFirebase(email,password);
    }
    public void singUpWithFirebase(String email,String password){
        if(email == "" || password == ""){
            Toast.makeText(RegistrazioneActivity.this, "Perfavore inserisci tutti i dati per procedere alla registrazione!",
                    Toast.LENGTH_SHORT).show();
        }else {
            mAuth.createUserWithEmailAndPassword(email, password) // l'istanza di firebeauth si preoccupa della registrazione
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { // la registrazione è andata a buon fine
                                Intent intent = new Intent(RegistrazioneActivity.this, NavigationActivity.class); // L'utente viene indirizzato alla home
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegistrazioneActivity.this, "Registrazione fallita! Prova di nuovo",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}