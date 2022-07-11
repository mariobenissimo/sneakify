package com.example.sneakify.FrontEndUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.sneakify.FrontEndUser.CarelloFragment;
import com.example.sneakify.FrontEndUser.HomeFragment;
import com.example.sneakify.FrontEndUser.PreferitiFragment;
import com.example.sneakify.FrontEndUser.ProfiloFragment;
import com.example.sneakify.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
// Classe Navigation che permette la navigation tra i diversi fragment
public class NavigationActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    PreferitiFragment preferitiFragment;
    CarelloFragment carelloFragment;
    ProfiloFragment profiloFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        setupLayout();
    }

    private void setupLayout() {
        bottomNavigationView = findViewById(R.id.bnView);
        homeFragment = new HomeFragment();
        preferitiFragment = new PreferitiFragment();
        carelloFragment = new CarelloFragment();
        profiloFragment = new ProfiloFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() { // listener al bottomnavigationView
                                                           @Override
                                                           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                                               switch (item.getItemId()){ // viene intercettato l'item cliccato ed in base all'id dell'item viene cambiata fragment
                                                                   case R.id.home:
                                                                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,homeFragment).commit();
                                                                       break;
                                                                   case R.id.preferiti:
                                                                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,preferitiFragment).commit();
                                                                       break;
                                                                   case R.id.carello:
                                                                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,carelloFragment).commit();
                                                                       break;
                                                                   case R.id.profilo:
                                                                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,profiloFragment).commit();
                                                                       break;
                                                               }
                                                               return true;
                                                           }
                                                       }

        );
    }
}