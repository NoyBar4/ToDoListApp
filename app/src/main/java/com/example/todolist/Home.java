package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference userRef,  tasksRef, myRef;
    static String currentUser = FirebaseAuth.getInstance().getUid();
    static String phoneNumber, UserName = "";

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addNewTask;
    static TextView Title, TasksForToday;

    Calendar c = Calendar.getInstance();

    ArrayList<Task> tasks;
    static ArrayList<Task> delitedTasks = new ArrayList<>();
    ArrayList<Tag> tags = new ArrayList<>();
    static String currentDate;
    static String[] tagArray = {"home", "work", "health", "study", "shopping"};

    RecyclerView recyclerViewTasks, recyclerViewTags;
    TaskAdapter taskAdapter;
    TagAdapter tagAdapter;

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tasks = new ArrayList<>();
        Title = findViewById(R.id.title);

        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("users").child(currentUser).child("profile");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    //Log.d("user",ds.getRef().toString());
                    if(ds.getKey().equals("phone")){
                        phoneNumber = ds.getValue().toString();
                        Log.d("phoneNumber",phoneNumber);
                    }
                    if(ds.getKey().equals("name")){
                    UserName = ds.getValue().toString();
                    Title.setText("What's Up " + UserName + " !");
                        Log.d("UserName",UserName);
                    }
                    if(ds.getKey().equals("task counter")){
                        Dashboard.countD = Integer.parseInt(ds.getValue().toString());
                    }
                    if(ds.getKey().equals("priority counter")){
                       Dashboard.countP1 = Integer.parseInt(ds.child("priority 1").getValue().toString());
                       Dashboard.countP2 = Integer.parseInt(ds.child("priority 2").getValue().toString());
                       Dashboard.countP3 = Integer.parseInt(ds.child("priority 3").getValue().toString());
                       Dashboard.countP4 = Integer.parseInt(ds.child("priority 4").getValue().toString());
                    }
                    /*
                    if(ds.getKey().equals("tags counter")){
                        Dashboard.countHome = Integer.parseInt(ds.child("home").getValue().toString());
                        Dashboard.countWork = Integer.parseInt(ds.child("work").getValue().toString());
                        Dashboard.countHealth = Integer.parseInt(ds.child("health").getValue().toString());
                        Dashboard.countStudy = Integer.parseInt(ds.child("study").getValue().toString());
                        Dashboard.countShopping = Integer.parseInt(ds.child("shopping").getValue().toString());
                    }
                     */
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        startAlarm();

        ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.miHome);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miCalender:
                        startActivity(new Intent(getApplicationContext(), Calender.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.miAllActivity:
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.miHome:
                        return true;
                }
                return true;
            }
        });

        currentDate = sdf.format(c.getTime());

        TasksForToday = findViewById(R.id.task_for_today);
        addNewTask = findViewById(R.id.fab);
        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTask();
            }
        });

        //TasksForToday.setText(String.format("you have %d tasks for today", tasks.size()));
        recyclerViewTasks = findViewById(R.id.tasks_recycler);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(tasks);
        recyclerViewTasks.setAdapter(taskAdapter);

        tags = new ArrayList<>();
        tags.add(new Tag("home", "home"));
        tags.add(new Tag("work", "work"));
        tags.add(new Tag("health", "health"));
        tags.add(new Tag("study", "study"));
        tags.add(new Tag("shopping", "shopping"));

        recyclerViewTags = findViewById(R.id.tags_recycler);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        tagAdapter = new TagAdapter(tags);
        recyclerViewTags.setAdapter(tagAdapter);

        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                    editExistTask(task, tasks.indexOf(task));
            }
        });

        myRef = db.getReference("users").child(currentUser);
        myRef.child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Log.d("task:" , dataSnapshot.getRef().toString());
                    // if(dataSnapshot.child(id).getValue() != null ) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if(task.getDate().equals(currentDate))
                        tasks.add(task);
                    //  }
                    //Log.d("tasks:" , dataSnapshot.getValue(Task.class).toString());
                }
                taskAdapter.notifyDataSetChanged();
                TasksForToday.setText(String.format("you have %d tasks for today", tasks.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        for(Task task : allTasks) {
            if (task.getDate().equals(currentDate)) {
                tasks.add(task);

            }
        }
         */

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void createNewTask() {

        final Dialog dialog = new Dialog(Home.this);
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
        tag.setText("Tags: none");

        NumberPicker myNumberPicker = new NumberPicker(Home.this);
        myNumberPicker.setMaxValue(4);
        myNumberPicker.setMinValue(1);
        myNumberPicker.setValue(4);

        ArrayList<Integer> tagList = new ArrayList<>();
        boolean[] selectedTag = new boolean[tagArray.length];

        Task newTask = new Task(" ", "", "", "", "flag4", "calender", tag.getText().toString(), "tag", false);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tasks.add(new Task(title.getText().toString(), description.getText().toString(), date.getText().toString(), "flag"+(int)myNumberPicker.getValue(), "calender", tag.getText().toString(), "tag", false));
                newTask.setTitle(title.getText().toString());
                newTask.setDescription(description.getText().toString());
                //newTask.setPriority(priority.getText().toString());
                newTask.setTags(tag.getText().toString());


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

                //Calender.allTasks.add(newTask);
                tasksRef = db.getReference().child("users").child(currentUser).child("tasks");
                String id = tasksRef.push().getKey();
                tasksRef.child(id).setValue(newTask);

                //if(newTask.getDate().equals(currentDate))
                  //  for (int i = 0; i<tasks.size(); i++)
                   //     tasks.remove(tasks.get(i));
                tasks.clear();
                taskAdapter.notifyDataSetChanged();

                dialog.dismiss();
                TasksForToday.setText(String.format("you have %d tasks for today", tasks.size()));
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
                            //newTask.setTime(alarm.getText().toString());
                        }
                    };

                    TimePickerDialog timePickerDialog = new TimePickerDialog(Home.this, onTimeSetListener, hour, minute, true);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(Home.this, new DatePickerDialog.OnDateSetListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this).setView(myNumberPicker);
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

        //final ArrayList<Integer>[] tagList = new ArrayList[]{new ArrayList<>()};

        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Select Tags");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(tagArray, selectedTag, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                        if (isChecked) {
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
                            //concat array value
                            stringBuilder.append(tagArray[tagList.get(j)]);
                            if (j != tagList.size() - 1)
                                stringBuilder.append(", ");
                        }
                        tag.setText("Tags: " + stringBuilder.toString());
                        newTask.setTags(stringBuilder.toString());
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
        final Dialog dialog = new Dialog(Home.this);
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
        NumberPicker myNumberPicker = new NumberPicker(Home.this);

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
                //tasks.set(position, newTask);
                //if (!date.getText().toString().equals(currentDate))
                //    tasks.remove(position);

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
                tasks.clear();
                changePriority(existTask, newTask);
                taskAdapter.notifyDataSetChanged();
                dialog.dismiss();
                //TasksForToday.setText(String.format("you have %d tasks for today", tasks.size()));
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
                            alarm.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        }
                    };

                    TimePickerDialog timePickerDialog = new TimePickerDialog(Home.this, onTimeSetListener, hour, minute, true);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(Home.this, new DatePickerDialog.OnDateSetListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this).setView(myNumberPicker);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
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
                        existTask.setTags(stringBuilder.toString());
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

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(Home.this);
                deleteDialog.setTitle("Are you sure you'd like to delete this task?");
                deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //tasks.remove(position);
                        //delitedTasks.add(existTask);
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

                        tasks.clear();
                        taskAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        TasksForToday.setText(String.format("you have %d tasks for today", tasks.size()));
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

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "taskReminderChannle";
            String description = "Channle For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("task",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startAlarm() {
        //createNotificationChannel();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        //calendar.add(Calendar.DATE,1);

        if(c.after(calendar))
            calendar.add(Calendar.DATE,1);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }
    }

    public void sendSMS (View view, String message, String phone){
        SmsManager mySmsManager = SmsManager.getDefault();
        mySmsManager.sendTextMessage(phone,null,message,null,null);
        //Toast.makeText(Home.this, "Message Sent successfully!", Toast.LENGTH_LONG).show();
    }

    public static void changePriority(Task oldT, Task newT)
    {
        //update counter about the new task
        if(newT.getPriority().equals("flag1")) {
            Dashboard.countP1++;
            DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 1");
            countRef.setValue(Dashboard.countP1);
        }
        if(newT.getPriority().equals("flag2")){
            Dashboard.countP2++;
            DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 2");
            countRef.setValue(Dashboard.countP2);
        }
        if(newT.getPriority().equals("flag3"))
        {
            Dashboard.countP3++;
            DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 3");
            countRef.setValue(Dashboard.countP3);
        }
        if(newT.getPriority().equals("flag4"))
        {
            Dashboard.countP4++;
            DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 4");
            countRef.setValue(Dashboard.countP4);
        }

        //update counter about the old task
        if(oldT.getPriority().equals("flag1")) {
            Dashboard.countP1--;
            DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 1");
            countRef.setValue(Dashboard.countP1);
        }
        if(oldT.getPriority().equals("flag2")){
            Dashboard.countP2--;
            DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 2");
            countRef.setValue(Dashboard.countP2);
        }
        if(oldT.getPriority().equals("flag3"))
        {
            Dashboard.countP3--;
            DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 3");
            countRef.setValue(Dashboard.countP3);
        }
        if(oldT.getPriority().equals("flag4"))
        {
            Dashboard.countP4--;
            DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("priority counter").child("priority 4");
            countRef.setValue(Dashboard.countP4);
        }
    }
}
