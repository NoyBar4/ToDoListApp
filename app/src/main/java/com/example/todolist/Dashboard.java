package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.todolist.Home.currentUser;

public class Dashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView counterWaiting;
    TextView counterDone;
    int countW = 0;
    static int countD;
    static int countP1, countP2, countP3, countP4;
    static int countHome, countWork, countHealth, countStudy, countShopping;

    DatabaseReference db;
    String currentUser = FirebaseAuth.getInstance().getUid();

    PieChart pieChart;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.miAllActivity);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miCalender:
                        startActivity(new Intent(getApplicationContext(), Calender.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.miAllActivity:
                        return true;
                }
                return true;
            }
        });

        //data #1
        counterWaiting = findViewById(R.id.count_waiting);
        db = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("tasks");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                   countW++;
                }
                counterWaiting.setText(String.valueOf(countW));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ///////////////////////////////////////////////////////////////////////

        //data #2
        counterDone = findViewById(R.id.count_done);
        DatabaseReference countTasksRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("task counter");
        countTasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                counterDone.setText(snapshot.getValue().toString());
                Log.d("counter",snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ///////////////////////////////////////////////////////////////////////

        //data #3
        pieChart = findViewById(R.id.pieChart);
        DatabaseReference countPriRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter");
        countPriRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    if(ds.getKey().equals("priority 1")){
                        countP1 = Integer.parseInt(ds.getValue().toString());
                        Log.d("priority 1",String.valueOf(countP1));
                    }
                    if(ds.getKey().equals("priority 2")){
                        countP2 = Integer.parseInt(ds.getValue().toString());
                        Log.d("priority 2",String.valueOf(countP2));
                    }
                    if(ds.getKey().equals("priority 3")){
                        countP3 = Integer.parseInt(ds.getValue().toString());
                        Log.d("priority 3",String.valueOf(countP3));
                    }
                    if(ds.getKey().equals("priority 4")){
                        countP4 = Integer.parseInt(ds.getValue().toString());
                        Log.d("priority 4",String.valueOf(countP4));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(countP1,"p1"));
        visitors.add(new PieEntry(countP2,"p2"));
        visitors.add(new PieEntry(countP3,"p3"));
        visitors.add(new PieEntry(countP4,"p4"));

        PieDataSet pieDataSet = new PieDataSet(visitors, "Priorities");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(15f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("how many tasks in each priority");
        pieChart.animate();
        ///////////////////////////////////////////////////////////////////////

        //data #4
        /*
        barChart = findViewById(R.id.barChart);
        DatabaseReference countTagRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("tags counter");
        countTagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    if(ds.getKey().equals("home")){
                        countHome = Integer.parseInt(ds.getValue().toString());
                    }
                    if(ds.getKey().equals("work")){
                        countWork = Integer.parseInt(ds.getValue().toString());
                    }
                    if(ds.getKey().equals("health")){
                        countHealth = Integer.parseInt(ds.getValue().toString());
                    }
                    if(ds.getKey().equals("study")){
                        countStudy = Integer.parseInt(ds.getValue().toString());
                    }
                    if(ds.getKey().equals("shopping")){
                        countShopping = Integer.parseInt(ds.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<BarEntry> visitors1 = new ArrayList<>();
        visitors1.add(new BarEntry(Float.valueOf("home"),1));
        visitors1.add(new BarEntry(Float.valueOf("work"),2));
        visitors1.add(new BarEntry(Float.valueOf("health"),3));
        visitors1.add(new BarEntry(Float.valueOf("study"),4));
        visitors1.add(new BarEntry(Float.valueOf("shopping"),5));

        BarDataSet barDataSet = new BarDataSet(visitors1, "Tags");
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(15f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animate();

         */
    }
}