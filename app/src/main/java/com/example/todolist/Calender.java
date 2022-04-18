package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import static com.example.todolist.Dashboard.countD;
import static com.example.todolist.Home.TasksForToday;
import static com.example.todolist.Home.currentDate;
import static com.example.todolist.Home.currentUser;
import static com.example.todolist.Home.phoneNumber;
import static com.example.todolist.Home.tagArray;

public class Calender extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addNewTask;

    RecyclerView recyclerTasks;
    TaskAdapter taskAdapter;
    ArrayList<Task> allTasks = new ArrayList<>();

    FirebaseDatabase db;
    DatabaseReference myRef, tasksRef;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        allTasks = new ArrayList<>();

        String currentuser = FirebaseAuth.getInstance().getUid();
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("users").child(currentuser);

        //for (Task task : allTasks)
        //myRef.child("tasks").child(String.valueOf(task.getTitle())).setValue(task);

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

                    case R.id.miAllActivity:
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.miCalender:
                        return true;
                }
                return false;
            }
        });

        addNewTask = findViewById(R.id.fab);
        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTask();
            }
        });

        recyclerTasks = findViewById(R.id.recycler);
        recyclerTasks.setHasFixedSize(true);
        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(allTasks);
        recyclerTasks.setAdapter(taskAdapter);


        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                if (task.isDone())
                    allTasks.remove(allTasks.indexOf(task));
                else
                    editExistTask(task, allTasks.indexOf(task));
            }
        });

        myRef.child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Log.d("task:" , dataSnapshot.getRef().toString());
                   // if(dataSnapshot.child(id).getValue() != null ) {
                    Task task = dataSnapshot.getValue(Task.class);
                    allTasks.add(task);
                  //  }
                    //Log.d("tasks:" , dataSnapshot.getValue(Task.class).toString());
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createNewTask() {

        final Dialog dialog = new Dialog(Calender.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_edit_task);
        //The user will be able to cancel the dialog by typing anywhere outside the dialog
        dialog.setCancelable(true);

        //Initializing the views of the dialog
        final EditText title = dialog.findViewById(R.id.ed_title);
        final EditText description = dialog.findViewById(R.id.ed_description);
        final TextView date = dialog.findViewById(R.id.ed_date);
        final Switch alarm = dialog.findViewById(R.id.alarm);
        final TextView priority = dialog.findViewById(R.id.tv_priority);
        final TextView tag = dialog.findViewById(R.id.tv_tag);
        final TextView delete = dialog.findViewById(R.id.delete);
        final ImageButton submit = dialog.findViewById(R.id.ib_send);
        tag.setText("Tags: none");

        NumberPicker myNumberPicker = new NumberPicker(Calender.this);
        myNumberPicker.setMaxValue(4);
        myNumberPicker.setMinValue(1);
        myNumberPicker.setValue(4);

        final ArrayList<Integer>[] tagList = new ArrayList[]{new ArrayList<>()};
        boolean[] selectedTag = new boolean[tagArray.length];

        Task newTask = new Task(" ", "", "", "", "flag4", "calender", tag.getText().toString(), "tag", false);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tasks.add(new Task(title.getText().toString(), description.getText().toString(), date.getText().toString(), "flag"+(int)myNumberPicker.getValue(), "calender", tag.getText().toString(), "tag", false));
                newTask.setTitle(title.getText().toString());
                newTask.setDescription(description.getText().toString());
                newTask.setTags(priority.getText().toString());
                newTask.setTags(tag.getText().toString());

                //String number = "0523394358";
                if(newTask.getPriority().equals("flag1")) {
                    Dashboard.countP1++;
                    DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 1");
                    countRef.setValue(Dashboard.countP1);
                    sendSMS(view, "Hurry Up! your task " + title.getText().toString() + " is due today", phoneNumber);
                }
                if(newTask.getPriority().equals("flag2")){
                    Dashboard.countP2++;
                    DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 2");
                    countRef.setValue(Dashboard.countP2);
                }
                if(newTask.getPriority().equals("flag3"))
                {
                    Dashboard.countP3++;
                    DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 3");
                    countRef.setValue(Dashboard.countP3);
                }
                if(newTask.getPriority().equals("flag4"))
                {
                    Dashboard.countP4++;
                    DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 4");
                    countRef.setValue(Dashboard.countP4);
                }

                //allTasks.add(newTask);
                tasksRef = db.getReference().child("users").child(currentUser).child("tasks");
                String id = tasksRef.push().getKey();
                tasksRef.child(id).setValue(newTask);

                allTasks.clear();
                taskAdapter.notifyDataSetChanged();
                dialog.dismiss();
                //TasksForToday.setText(String.format("you have %d tasks for today", tasks.size()));
            }
        });

        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int hour = c.get(Calendar.YEAR);
            int minute = c.get(Calendar.MONTH);

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourSelector, int minuteSelector) {
                            hour = hourSelector;
                            minute = minuteSelector;
                            alarm.setText(String.format(Locale.getDefault(), "%02d:%02d", hour,minute));
                        }
                    };

                    TimePickerDialog timePickerDialog = new TimePickerDialog(Calender.this, onTimeSetListener, hour, minute, true);
                    timePickerDialog.setTitle("select time");
                    timePickerDialog.show();
                } else {

                }
            }
        });

        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Home.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

                 */
                DatePickerDialog datePickerDialog = new DatePickerDialog(Calender.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //month = month + 1;
                        //String final_date = dayOfMonth + " / " + month + " / " + year;
                        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                        date.setText(sdf.format(calendar.getTime()));
                        newTask.setDate(date.getText().toString());
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumberPicker.OnValueChangeListener myOnValueChangeListener = new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    }
                };

                myNumberPicker.setOnValueChangedListener(myOnValueChangeListener);
                AlertDialog.Builder builder = new AlertDialog.Builder(Calender.this).setView(myNumberPicker);
                builder.setTitle("Priority");
                builder.setPositiveButton("End", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        newTask.setPriority("flag" + (int) myNumberPicker.getValue());
                        priority.setText("flag" + (int) myNumberPicker.getValue());
                    }
                });
                builder.show();
            }
        });

        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Calender.this);
                builder.setTitle("Select Tags");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(tagArray, selectedTag, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            //when checkbox selected add position from the list
                            tagList[0].add(i);
                            Collections.sort(tagList[0]);
                        } else
                            //when checkbox unselected remove position from the list
                            tagList[0].remove(i);
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < tagList[0].size(); j++) {
                            //conact array value
                            stringBuilder.append(tagArray[tagList[0].get(j)]);
                            if (j != tagList[0].size() - 1)
                                stringBuilder.append(", ");
                        }
                        tag.setText("Tags: " + stringBuilder.toString());
                        //newTask.setTagsList(stringBuilder);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        delete.setVisibility(View.INVISIBLE);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void editExistTask(Task existTask, int position) {
        final Dialog dialog = new Dialog(Calender.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_edit_task);
        //The user will be able to cancel the dialog by typing anywhere outside the dialog
        //dialog.setCancelable(true);

        //Initializing the views of the dialog
        final EditText title = dialog.findViewById(R.id.ed_title);
        final EditText description = dialog.findViewById(R.id.ed_description);
        final TextView date = dialog.findViewById(R.id.ed_date);
        final Switch alarm = dialog.findViewById(R.id.alarm);
        final TextView priority = dialog.findViewById(R.id.tv_priority);
        final TextView tag = dialog.findViewById(R.id.tv_tag);
        final TextView delete = dialog.findViewById(R.id.delete);
        final ImageButton submit = dialog.findViewById(R.id.ib_send);
        NumberPicker myNumberPicker = new NumberPicker(Calender.this);

        title.setText(existTask.getTitle());
        description.setText(existTask.getDescription());
        date.setText(existTask.getDate());

        final String prioritySri = existTask.getPriority();
        Character priorityCh = prioritySri.charAt(prioritySri.length() - 1);
        int priorityNum = Character.getNumericValue(priorityCh);
        myNumberPicker.setMaxValue(4);
        myNumberPicker.setMinValue(1);
        myNumberPicker.setValue(priorityNum);

        ArrayList<Integer> tagList = new ArrayList<>();
        boolean[] selectedTag = new boolean[tagArray.length];

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task newTask = new Task(title.getText().toString(), description.getText().toString(), date.getText().toString(), alarm.getText().toString(), "flag" + myNumberPicker.getValue(), "calender", tag.getText().toString(), "tag", false);
                //allTasks.set(position, newTask);

                //if (!date.getText().toString().equals(currentDate))
                //    allTasks.remove(position);

                tasksRef = db.getReference().child("users").child(currentUser).child("tasks");
                        tasksRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren())
                                {
                                    if(ds.child("title").getValue() == existTask.getTitle() ){
                                        if(ds.child("description").getValue() == existTask.getDescription() ){
                                            String id = ds.getKey();
                                            //DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser).child("tasks").child(id).getRef();
                                            //deleteRef.removeValue();
                                            tasksRef.child(id).setValue(newTask);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                allTasks.clear();
                Home.changePriority(existTask, newTask);
                taskAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourSelector, int minuteSelector) {
                            hour = hourSelector;
                            minute = minuteSelector;
                            alarm.setText(String.format(Locale.getDefault(), "%02d:%02d",hour,minute));
                        }
                    };

                    TimePickerDialog timePickerDialog = new TimePickerDialog(Calender.this, onTimeSetListener, hour, minute, true);
                    timePickerDialog.setTitle("select time");
                    timePickerDialog.show();
                } else {

                }
            }
        });

        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Home.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

                 */
                DatePickerDialog datePickerDialog = new DatePickerDialog(Calender.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        /*
                        month = month + 1;
                        String final_date = dayOfMonth + " / " + month + " / " + year;
                        date.setText(final_date);
                         */
                        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                        date.setText(sdf.format(calendar.getTime()));
                        existTask.setDate(date.getText().toString());
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myNumberPicker.setMaxValue(4);
                myNumberPicker.setMinValue(1);
                NumberPicker.OnValueChangeListener myOnValueChangeListener = new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    }
                };

                myNumberPicker.setOnValueChangedListener(myOnValueChangeListener);
                AlertDialog.Builder builder = new AlertDialog.Builder(Calender.this).setView(myNumberPicker);
                builder.setTitle("Priority");
                builder.setPositiveButton("End", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    }
                });
                builder.show();
            }
        });

        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Calender.this);
                builder.setTitle("Select Tags");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(tagArray, selectedTag, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            //when checkbox selected add position from the list
                            tagList.add(i);
                            Collections.sort(tagList);
                        } else
                            //when checkbox unselected remove position from the list
                            tagList.remove(i);
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < tagList.size(); j++) {
                            //conact array value
                            stringBuilder.append(tagArray[tagList.get(j)]);
                            if (j != tagList.size() - 1)
                                stringBuilder.append(", ");
                        }
                        tag.setText("Tags: " + stringBuilder.toString());
                        //existTask.setTagsList(stringBuilder);
                    }
                });

                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(Calender.this);
                deleteDialog.setTitle("Are you sure you'd like to delete this task?");
                deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //delitedTasks.add(existTask);
                        //TasksForToday.setText(String.format("you have %d tasks for today", tasks.size()));

                        tasksRef = db.getReference().child("users").child(currentUser).child("tasks");
                        tasksRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren())
                                {
                                    if(ds.child("title").getValue() == existTask.getTitle() ){
                                        if(ds.child("description").getValue() == existTask.getDescription() ){
                                            String id = ds.getKey();
                                            DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser).child("tasks").child(id).getRef();
                                            deleteRef.removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        allTasks.clear();
                        taskAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        //Log.d("remove",taskRef.toString());
                    }
                });
                deleteDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                deleteDialog.show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void sendSMS (View view, String message, String phone){
        SmsManager mySmsManager = SmsManager.getDefault();
        mySmsManager.sendTextMessage(phone,null,message,null,null);
        //Toast.makeText(Home.this, "Message Sent successfully!", Toast.LENGTH_LONG).show();
    }

}