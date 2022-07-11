package com.example.sneakify.BackEndGestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.sneakify.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

//classe che permette la navigazione tra le fragment
public class NavigationGestoreActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    AddItemFragment addItemFragment;
    ShopFragment shopFragment;
    ProfiloGestoreFragment profiloGestoreFragment;
    AddBrandFragment addBrandFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_gestore);
        bottomNavigationView = findViewById(R.id.bnViewGestore);
        addItemFragment = new AddItemFragment();
        shopFragment = new ShopFragment();
        profiloGestoreFragment = new ProfiloGestoreFragment();
        addBrandFragment = new AddBrandFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, shopFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                                                           @Override
                                                           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                                               switch (item.getItemId()) {
                                                                   case R.id.ShopGestore:
                                                                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, shopFragment).commit();
                                                                       break;
                                                                   case R.id.AddGestore:
                                                                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, addItemFragment).commit();
                                                                       break;
                                                                   case R.id.addBrandIcon:
                                                                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, addBrandFragment).commit();
                                                                       break;
                                                                   case R.id.profiloGestore:
                                                                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profiloGestoreFragment).commit();
                                                                       break;
                                                               }
                                                               return true;
                                                           }
                                                       }
        );
    }
}