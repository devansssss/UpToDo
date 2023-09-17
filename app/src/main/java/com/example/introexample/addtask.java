package com.example.introexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class addtask extends AppCompatActivity {
    FirebaseAuth auth;
    private EditText edittexttasktitle, edittexttaskdescription;
    FirebaseUser user;
    private Calendar selectedDate;
    FirebaseFirestore db;
    private Button setduedatebtn, setduetimebtn, addtaskfinal, setprioritybtn;
    private String stringdate;
    private TextView selecteddate, selectedtime;
    DocumentReference userTasksDocument;
    CollectionReference tasksCollection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        setduetimebtn = findViewById(R.id.setduetimebtn);
        addtaskfinal = findViewById(R.id.addtaskfinal);
        setduedatebtn = findViewById(R.id.setduedatebtn);
        selecteddate = findViewById(R.id.selecteddate);
        selectedtime = findViewById(R.id.selectedtime);
        edittexttasktitle = findViewById(R.id.addtasktitle);
        edittexttaskdescription = findViewById(R.id.addtaskdescription);
        setprioritybtn = findViewById(R.id.setprioritybtn);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String id = user.getUid();
        db = FirebaseFirestore.getInstance();
        Task task = new Task();
        DocumentReference userdocument = db.collection("users").document(id);
        CollectionReference tasks = userdocument.collection("tasks");
        DocumentReference newTaskDocument = tasks.document();
        addtaskfinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(edittexttasktitle.getText()).isEmpty()){
                    Toast.makeText(addtask.this, "Add a title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (String.valueOf(edittexttaskdescription.getText()).isEmpty()){
                    Toast.makeText(addtask.this, "Add a description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (String.valueOf(selecteddate.getText()).isEmpty()){
                    Toast.makeText(addtask.this, "Enter Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (String.valueOf(selectedtime.getText()).isEmpty()){
                    Toast.makeText(addtask.this, "Enter Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (task.getPriority()!=1 && task.getPriority()!=2 && task.getPriority()!=3 && task.getPriority()!=4){
                    Toast.makeText(addtask.this, "Please select task priority", Toast.LENGTH_SHORT).show();
                    return;
                }
                task.setTitle(edittexttasktitle.getText().toString());
                task.setDescription(edittexttaskdescription.getText().toString());
                Map<String, Object> updates = new HashMap<>();
                if (task.getDescription().length()>200){
                    Toast.makeText(addtask.this, "Description too big!", Toast.LENGTH_SHORT).show();
                    return;
                }
                newTaskDocument.set(task)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                updates.put("title", task.getTitle());
                                updates.put("dueTime", task.getDueTime());
                                updates.put("description", task.getDescription());
                                updates.put("dueDate", task.getDueDate());
                                String uid = newTaskDocument.getId();
                                task.setUID(uid);
                                updates.put("UID", uid);
                                updates.put("Priority", task.getPriority());
                                newTaskDocument.set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(addtask.this, "Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(addtask.this, "failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        setduedatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(addtask.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int selectedYear = year;
                        int selectedMonth = month;
                        int selectedDay = dayOfMonth;
                        selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        stringdate = String.valueOf(selectedDay) + String.valueOf(selectedMonth) + String.valueOf(selectedYear);
                        selecteddate.setText(stringdate);
                        task.setDueDate(selectedDate.getTime());
                        if (task.getDueDate()!=null){
                            setduedatebtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.purpcalendar));
                        }
                    }
                },year, month, day);
                datePickerDialog.show();
            }
        });



        setduetimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(addtask.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int selectedhour = hourOfDay;
                        int selectedminute = minute;
                        String Selectime = String.valueOf(selectedhour) +":" + String.valueOf(selectedminute);
                        selectedtime.setText(Selectime);
                        task.setDueTime(selectedtime.getText().toString());
                        if (!task.getDueTime().isEmpty()){
                            setduetimebtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.purptime));
                        }
                    }
                },hour, minute, false);
                timePickerDialog.show();
            }
        });




        setprioritybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(addtask.this);
                View dialogview = getLayoutInflater().inflate(R.layout.setprioritydialoglayout, null);
                builder.setView(dialogview);
                AlertDialog dialog = builder.create();
                RadioGroup radioGroupPriority = dialogview.findViewById(R.id.radiogrouppriority);
                TextView setpriority = dialogview.findViewById(R.id.setpriority);
                TextView cancelpriority = dialogview.findViewById(R.id.cancelsetpriority);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(00000000));
                setpriority.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        int checkecradiobtnid = radioGroupPriority.getCheckedRadioButtonId();
                        switch (checkecradiobtnid){
                            case R.id.priority1:
                                task.setPriority(1);
                                setprioritybtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.purpflag));
                                break;
                            case R.id.priority2:
                                task.setPriority(2);
                                setprioritybtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.purpflag));
                                break;
                            case R.id.priority3:
                                task.setPriority(3);
                                setprioritybtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.purpflag));
                                break;
                            case R.id.priority4:
                                task.setPriority(4);
                                setprioritybtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.purpflag));
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                cancelpriority.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }
}