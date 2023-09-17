package com.example.introexample;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    Button logoutbtn, tasksleft, changenamebtn, changepassbtn;
    FirebaseAuth mauth;

    FirebaseUser user;
    private CollectionReference taskscollection;
    private String userid;
    private FirebaseFirestore db;
    EditText editaccname, editaccpassword, changeaccpassword;
    private String email;
    View view;
    TextView editaccnamebtn, accname, editaccpasswordbtn, changeaccpasswordbtn;
    DocumentReference userdocument;

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String cachedName = preferences.getString("Name", null);
        String cachedTasks = preferences.getString("Tasks", null);
        if (cachedName != null && cachedTasks!=null) {
            accname.setText(cachedName);
            tasksleft.setText(cachedTasks + " TASKS LEFT");
        } else if(cachedName==null){
            userdocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        String name = task.getResult().getString("Name");
                        accname.setText(name);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Name", name);
                        editor.apply();
                    }
                    else {
                        Toast.makeText(getContext(), "Failed to get name", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (cachedTasks==null){
            taskscollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                String tasks;
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        tasks = String.valueOf(task.getResult().size());
                        SharedPreferences preferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Tasks", tasks);
                        editor.apply();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "failed to get tasks", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        tasksleft = view.findViewById(R.id.numberoftasksleft);
        mauth = FirebaseAuth.getInstance();
        changepassbtn = view.findViewById(R.id.changepasswordbtn);
        changenamebtn = view.findViewById(R.id.changenamebtn);
        db = FirebaseFirestore.getInstance();
        userid = mauth.getUid();
        if (user!=null){
            email = user.getEmail();
        }
        userdocument = db.collection("users").document(userid);
        Map<String, Object> userData = new HashMap<>();
        accname = view.findViewById(R.id.profilename);
        userdocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    SharedPreferences preferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    String name = task.getResult().getString("Name");
                    editor.putString("Name", name);
                    editor.commit();
                    accname.setText(name);
                }
                else {
                    Toast.makeText(getContext(), "Failed to get name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        taskscollection = db.collection("users").document(userid).collection("tasks");
        getremainingtasks();
        user = mauth.getCurrentUser();
        logoutbtn = view.findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to log out?");
                builder.setTitle("Log Out");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener)(dialog, which) -> {
                    Intent intent = new Intent(getContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    mauth.signOut();
                });
                builder.setNegativeButton("No", (DialogInterface.OnClickListener)(dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        changepassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.getpassworddialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                editaccpassword = dialog.findViewById(R.id.editaccpassword);
                editaccpasswordbtn = dialog.findViewById(R.id.editaccpasswordtext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(00000000));
                email = user.getEmail();
                editaccpasswordbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editaccpassword.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(), "please enter password", Toast.LENGTH_SHORT).show();
                        }else {
                            String pass = editaccpassword.getText().toString();
                            AuthCredential credential = EmailAuthProvider.getCredential(email,pass);
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Dialog dialog2 = new Dialog(getActivity());
                                        dialog2.setContentView(R.layout.changepassworddialog);
                                        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        changeaccpassword = dialog2.findViewById(R.id.changeaccpassword);
                                        changeaccpasswordbtn = dialog2.findViewById(R.id.changeaccpasswordtext);
                                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(00000000));
                                        changeaccpasswordbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (changeaccpassword.getText().toString().isEmpty()){
                                                    Toast.makeText(getActivity(), "please enter password", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }else {
                                                    String newpass = changeaccpassword.getText().toString();
                                                    user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(getContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                                                                dialog2.dismiss();
                                                                dialog.dismiss();
                                                            }else {
                                                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                        dialog2.show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                dialog.show();
            }
        });

        changenamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.usernamechangedialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                editaccname = dialog.findViewById(R.id.editaccname);
                editaccnamebtn = dialog.findViewById(R.id.editaccnametext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(00000000));
                editaccnamebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editaccname.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(), "please enter name", Toast.LENGTH_SHORT).show();
                        }else {
                            userData.put("Name", editaccname.getText().toString());
                            userData.put("Email", mauth.getCurrentUser().getEmail());
                            userdocument.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Name Saved!", Toast.LENGTH_SHORT).show();
                                    accname.setText(editaccname.getText().toString());
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
                dialog.show();
            }
        });
         return view;
    }

    private void getremainingtasks(){
        taskscollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            String tasks;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    tasks = String.valueOf(task.getResult().size());
                    SharedPreferences preferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Tasks", tasks);
                    editor.commit();
                    tasksleft.setText(tasks.toString() +" TASKS LEFT");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "failed to get tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}