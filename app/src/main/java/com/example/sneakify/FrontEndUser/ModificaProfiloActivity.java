package com.example.sneakify.FrontEndUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.sneakify.Adapter.ViewPageAdapter;
import com.example.sneakify.R;
import com.google.android.material.tabs.TabLayout;

// classe che permette di implementare la top toolbar e la possibilità di cambiare tra le due fragment predisposte
public class ModificaProfiloActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);
        tabLayout = findViewById(R.id.tbProfilo);
        viewPager = findViewById(R.id.vpProfilo);
        tabLayout.setupWithViewPager(viewPager);
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragment(new ModficaEmailFragment() , "Modifica Email");
        viewPageAdapter.addFragment(new ModificaPasswordFragment() , "Modifica Password");
        viewPager.setAdapter(viewPageAdapter);
    }
}