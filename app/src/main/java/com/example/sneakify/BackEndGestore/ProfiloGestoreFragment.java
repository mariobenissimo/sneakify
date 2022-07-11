package com.example.sneakify.BackEndGestore;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sneakify.FrontEndUser.ModificaProfiloActivity;
import com.example.sneakify.R;
import com.example.sneakify.SchermataIniziale.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

//classe che mostra i button per modificare email o password. Da questa schermata è anche possibile effettuare il logout
public class ProfiloGestoreFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private Button blogout;
    private Button bImpostazioniProfilo;
    public ProfiloGestoreFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilo_gestore, container, false);
        blogout = view.findViewById(R.id.bLogoutGestore);
        bImpostazioniProfilo = view.findViewById(R.id.bImpostazioniProfiloGestore);
        mFirebaseAuth = FirebaseAuth.getInstance();
        blogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        bImpostazioniProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ModificaProfiloActivity.class); // viene indirizzato alla activity che permette di modificare l'email e la password uguale a quella dell'utente
                startActivity(intent);
            }
        });
        return view;
    }
}