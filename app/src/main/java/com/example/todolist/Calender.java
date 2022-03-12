package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Calender extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerTasks;
    TaskAdapter taskAdapter;
    static ArrayList<Task> allTasks = new ArrayList<>();

    FirebaseDatabase db;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        String currentuser = FirebaseAuth.getInstance().getUid();
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference().child("users").child(currentuser);

        for (Task task : allTasks)
            myRef.child("tasks").child(String.valueOf(task.getTitle())).setValue(task);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.miCalender);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.miCalender:
                        return true;
                }
                return false;
            }
        });

        recyclerTasks = findViewById(R.id.recycler);
        recyclerTasks.setHasFixedSize(true);
        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(allTasks);
        recyclerTasks.setAdapter(taskAdapter);

        //DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        //loadTasks();
    }

    private void loadTasks() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getValue() != null) {
                        List ingredients = new ArrayList<>();
                        for (DataSnapshot ing : postSnapshot.child("tasks").getChildren()) {
                            String title = ing.child("title").getValue(String.class);
                            String description = ing.child("description").getValue(String.class);
                            String date = ing.child("date").getValue(String.class);
                            String time = ing.child("time").getValue(String.class);
                            String priority = ing.child("priority").getValue(String.class);
                            // Using your overloaded class constructor to populate the Order data
                            Task task = new Task(title,description,date,time,priority,"","","",false);

                            Log.e(TAG, "Gained data: " + title);
                        }
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}