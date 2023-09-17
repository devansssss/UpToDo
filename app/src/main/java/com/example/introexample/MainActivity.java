package com.example.introexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth auth;
    FirebaseUser user;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton addtaskbtn;
    FirebaseFirestore db;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.Bottomnavmenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.index);
        user = auth.getCurrentUser();
        userid = user.getUid();
        addtaskbtn = findViewById(R.id.addtaskbtn);

        if (user==null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }



        addtaskbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), addtask.class);
                intent.putExtra("intentstart", "main");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.index:
                ReplaceFragment(new HomeFragment());
                return true;
            case R.id.profile:
                ReplaceFragment(new ProfileFragment());
                return true;
            case R.id.calendar:
                ReplaceFragment(new calendarfragment());
                return true;
            case R.id.focus:
                ReplaceFragment(new FocusFragment());
                return true;
        }
        return false;
    }


    public void ReplaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }
}