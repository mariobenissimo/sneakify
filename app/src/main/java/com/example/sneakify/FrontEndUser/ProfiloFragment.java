package com.example.sneakify.FrontEndUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sneakify.SchermataIniziale.LoginActivity;
import com.example.sneakify.R;
import com.google.firebase.auth.FirebaseAuth;

//classe che mostra i button per andare alla lista dei preferiti/carello o modificare email o password. Da questa schermata è anche possibile effettuare il logout
public class ProfiloFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private Button blogout;
    private Button bPreferiti;
    private Button bCarello;
    private Button bImpostazioni;
    private PreferitiFragment preferitiFragment;
    private CarelloFragment carelloFragment;

    public ProfiloFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance(); // istanza del firebase database degli utenti
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profilo, container, false);
        setupLayout(view);
        return view;
    }
    void setupLayout(View view){
        blogout = view.findViewById(R.id.bLogoutUtente);
        bCarello = view.findViewById(R.id.bCarelloProfilo);
        bImpostazioni = view.findViewById(R.id.bImpostazioniProfilo);
        bPreferiti = view.findViewById(R.id.bPreferitiProfilo);
        preferitiFragment = new PreferitiFragment();
        carelloFragment = new CarelloFragment();
        blogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut(); // logout
                Intent intent = new Intent(getContext(), LoginActivity.class); // ritorno al login
                startActivity(intent);
            }
        });
        bPreferiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout,preferitiFragment).commit();
            }
        });
        bCarello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout,carelloFragment).commit();
            }
        });
        bImpostazioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ModificaProfiloActivity.class);
                startActivity(intent);
            }
        });
    }
}