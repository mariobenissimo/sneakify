package com.example.sneakify.SchermataIniziale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sneakify.FrontEndUser.NavigationActivity;
import com.example.sneakify.BackEndGestore.NavigationGestoreActivity;
import com.example.sneakify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
// classe che permette di fare il login
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private View view;
    private EditText etEmail;
    private EditText etPassowrd;
    private Button bLogin;
    private TextView tvAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        setupLayout(); // funzione utilizzata per inizializzare il layout grafico
    }
    void setupLayout(){ // vari collegamenti tra il layout grafico e il codice
        view = (View) findViewById(R.id.viewBackground);
        etEmail = (EditText) findViewById(R.id.etEmailLogin);
        etPassowrd = (EditText) findViewById(R.id.etPassLogin);
        bLogin = (Button) findViewById(R.id.bLogin);
        tvAccount = (TextView) findViewById(R.id.tvCreateAccount);
    }
    public void goToReg(View v){ // se invece si necessita di creare un utente verrà presentata l'activity di registrazione
        Intent intent = new Intent(LoginActivity.this, RegistrazioneActivity.class);
        startActivity(intent);
    }
    public void SignIn(View v){
        String email = etEmail.getText().toString();
        String password = etPassowrd.getText().toString();
        singInWithFirebase(email,password);
    }
    public void singInWithFirebase(String email,String password){
        if(email == "" || password == ""){
            Toast.makeText(LoginActivity.this, "Perfavore inserisci tutti i dati per procedere all'autenticazione!",
                    Toast.LENGTH_SHORT).show();
        }else {
            mAuth.signInWithEmailAndPassword(email, password) // l'istanza di firebeauth si preoccupa dell'autenticazione
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { // l'autenticazione è andata a buon fine
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user.getUid().equals("kXsajbqj3uNEV1quDcOofTaW2sv2")) { // Se l'utente appena loggato è l'admin viene indirizzato al frontend del gestore
                                    Intent intent = new Intent(LoginActivity.this, NavigationGestoreActivity.class);
                                    startActivity(intent);
                                } else { // in caso contrario viene indirizzato alla home del fronted lato user
                                    Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                                    startActivity(intent);
                                }
                            } else { // in caso contrario l'autenticazione nonn è andata a buon fine e viene presentato un toast di notifa
                                Toast.makeText(LoginActivity.this, "Autenticazione fallita! Prova di nuovo.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}