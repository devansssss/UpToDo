package com.example.introexample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment implements presentDayTaskAdapter.onTaskItemClickListener{
    FirebaseAuth auth;
    FirebaseUser user;
    TextView notasktext, addtaskstext;
    ImageView notaskimage;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton addtaskbtn;
    FirebaseFirestore db;
    String userid;
    CollectionReference taskscollection;
    RecyclerView presentdaytasksrv;
    presentDayTaskAdapter presentDayTaskAdapter;

    View view;
    private boolean isTasksLoaded = false;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        presentdaytasksrv = view.findViewById(R.id.presentdaytasksrv);
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        userid = user.getUid();
        taskscollection = db.collection("users").document(userid).collection("tasks");
        addtaskbtn = view.findViewById(R.id.addtaskbtn);
        presentdaytasksrv.setLayoutManager(new LinearLayoutManager(getContext()));
        presentDayTaskAdapter = new presentDayTaskAdapter(new ArrayList<>());
        presentdaytasksrv.setAdapter(presentDayTaskAdapter);
        progressBar = view.findViewById(R.id.progressBar);
        presentDayTaskAdapter.setOnTaskItemClickListener(this);
        notaskimage = view.findViewById(R.id.Notaskimage);
        notasktext = view.findViewById(R.id.notasktext);
        addtaskstext = view.findViewById(R.id.addtaskstext);
        if (user==null){
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
        }


        addtaskbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), addtask.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isTasksLoaded){
            showLoadingIndicator();
            loadTasksForPresentDay();
        }
    }
    private void showLoadingIndicator() {
        progressBar.setVisibility(View.VISIBLE);
        presentdaytasksrv.setVisibility(View.GONE);
        notaskimage.setVisibility(View.GONE);
        notasktext.setVisibility(View.GONE);
        addtaskstext.setVisibility(View.GONE);
    }
    private void showTaskList() {
        progressBar.setVisibility(View.GONE);
        presentdaytasksrv.setVisibility(View.VISIBLE);
        notaskimage.setVisibility(View.GONE);
        notasktext.setVisibility(View.GONE);
        addtaskstext.setVisibility(View.GONE);
    }

    private void showNoTasksMessage() {
        progressBar.setVisibility(View.GONE);
        presentdaytasksrv.setVisibility(View.GONE);
        notaskimage.setVisibility(View.VISIBLE);
        notasktext.setVisibility(View.VISIBLE);
        addtaskstext.setVisibility(View.VISIBLE);
    }

    private void loadTasksForPresentDay(){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        Date startTime = calendar.getTime();

        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endTime = calendar.getTime();

        Query query = taskscollection.whereGreaterThanOrEqualTo("dueDate", startTime).whereLessThanOrEqualTo("dueDate", endTime);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        Collections.sort(tasks);
                        tasks.add(task1);
                        presentDayTaskAdapter.setTaskList(tasks);
                        presentDayTaskAdapter.notifyDataSetChanged();
                    }
                    if (!tasks.isEmpty()){
                        isTasksLoaded = true;
                        showTaskList();

                    }
                    else{
                        showNoTasksMessage();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Error retrieving tasks", Toast.LENGTH_SHORT).show();
                }
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