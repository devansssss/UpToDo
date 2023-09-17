package com.example.introexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class intro extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String PREFS_NAME = "MyPrefsFile";
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            settings.edit().putBoolean("my_first_time", false).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_container, new onboarding()).commit();
            setContentView(R.layout.activity_intro);
        }
        else{
            if (user!=null){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_container, new StartScreen()).commit();
                setContentView(R.layout.activity_intro);
            }
        }
    }
}