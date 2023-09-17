package com.example.introexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskDetail extends AppCompatActivity {
    private TextView tasktitle, taskdescription, tasktime, taskpriority, taskdate;
    private Button deletetaskbtn;

    FirebaseAuth mauth;
    FirebaseFirestore db;
    String uid;
    CollectionReference taskscollection;
    DocumentReference task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        mauth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        uid = mauth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        taskscollection = db.collection("users").document(uid).collection("tasks");
        String s = intent.getStringExtra("uid");
        task = taskscollection.document(intent.getStringExtra("uid"));
        tasktitle = findViewById(R.id.taskdetailtasktitle);
        taskdescription = findViewById(R.id.taskdetailtaskdescription);
        tasktime = findViewById(R.id.taskdetailtasktime);
        taskpriority = findViewById(R.id.taskdetailtaskprio);
        taskdate = findViewById(R.id.taskdetailtaskdate);
        deletetaskbtn = findViewById(R.id.deletetask);


        deletetaskbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = taskscollection.whereEqualTo("uid", intent.getStringExtra("uid"));
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Toast.makeText(TaskDetail.this, "Please Wait!", Toast.LENGTH_SHORT).show();
                        DocumentReference documentReference = taskscollection.document(intent.getStringExtra("uid"));
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(TaskDetail.this, "Deleted", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent1);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TaskDetail.this, "failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });



        if (intent!=null){
            tasktitle.setText(intent.getStringExtra("Title"));
            taskdescription.setText(intent.getStringExtra("desc"));
            tasktime.setText(intent.getStringExtra("time"));
            taskpriority.setText(intent.getStringExtra("priority"));
            String d;
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE dd-MMM", Locale.ENGLISH);
                Date date = inputFormat.parse(String.valueOf(intent.getStringExtra("date")));
                d = outputFormat.format(date);
                taskdate.setText(d);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}