package com.example.introexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private TextInputEditText edittextemail, edittextpassword;
    private Button registerbtn;
    FirebaseAuth mAuth;
    private TextView loginonregister;




    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginonregister = findViewById(R.id.Loginonregister);
        edittextemail = findViewById(R.id.emailregister);
        edittextpassword = findViewById(R.id.passwordregister);
        mAuth = FirebaseAuth.getInstance();
        registerbtn = findViewById(R.id.userregister);
        loginonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = edittextemail.getText().toString();
                password = edittextpassword.getText().toString();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    FirebaseUser user1 = mAuth.getCurrentUser();
                                    String uid = user1.getUid();
                                    DocumentReference userDocument = db.collection("users").document(uid);
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", email);
                                    userDocument.set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Register.this, e.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e.toString().contains("The given password is invalid.")){
                                    Toast.makeText(getApplicationContext(), "The given password is invalid, try a longer password", Toast.LENGTH_LONG).show();
                                } else if (e.toString().contains("The email address is already in use by another account.")) {
                                    Toast.makeText(getApplicationContext(), "The email address is already in use by another account.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}