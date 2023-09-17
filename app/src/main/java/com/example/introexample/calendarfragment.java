package com.example.introexample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class calendarfragment extends Fragment implements CalendarRVAdapter.onTaskItemClickListener {
    View view;
    FirebaseAuth mauth;
    FirebaseFirestore db;
    private String uid;

    RecyclerView calendarrv;
    CalendarRVAdapter calendarRVAdapter;
    CollectionReference taskscollection;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendarfragment, container, false);
        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mauth.getCurrentUser().getUid();
        calendarrv = view.findViewById(R.id.Calendarrv);
        calendarrv.setLayoutManager(new LinearLayoutManager(getContext()));
        calendarRVAdapter = new CalendarRVAdapter(new ArrayList<>());
        calendarrv.setAdapter(calendarRVAdapter);
        taskscollection = db.collection("users").document(uid).collection("tasks");
        calendarRVAdapter.setOnTaskItemClickListener(this::onTaskItemClick);
        loadalltasks();
        return view;
    }

    private void loadalltasks() {
        taskscollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    List<Task> tasks = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        Date date = documentSnapshot.getDate("dueDate");
                        String time = documentSnapshot.getString("dueTime");
                        String uid = documentSnapshot.getString("uid");
                        int priority = documentSnapshot.getLong("priority").intValue();
                        com.example.introexample.Task task1 = new com.example.introexample.Task(title,description, date, time, uid, priority);
                        tasks.add(task1);
                        Collections.sort(tasks);
                        calendarRVAdapter.setTaskListCalendar(tasks);
                        calendarRVAdapter.notifyDataSetChanged();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskItemClick(Task task) {
        Intent intent = new Intent(getContext(), TaskDetail.class);
        intent.putExtra("Title", task.getTitle());
        intent.putExtra("desc", task.getDescription());
        intent.putExtra("time", task.getDueTime());
        intent.putExtra("uid", task.getUID());
        intent.putExtra("priority", String.valueOf(task.getPriority()));
        intent.putExtra("date", String.valueOf(task.getDueDate()));
        startActivity(intent);
    }
}