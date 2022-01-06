package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todolist.TaskAdapter.TaskViewHolder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import static com.example.todolist.TaskAdapter.*;

public class Today extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {

    ArrayList<Task> tasks;
    Button addNewTask;
    Calendar c;

    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    String currentDate;
    TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        //getSupportActionBar().hide();
        addNewTask = findViewById(R.id.add_new_task);
        c = Calendar.getInstance();

        tasks = new ArrayList<>();
        for (int i = 0; i <10; i++){
            tasks.add(new Task("Task " + (i+1), "Description " + (i+1), "23rd Nov", "flag"+(i / 3 + 1), "calender"+(i % 2 + 1), false));
        }

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(tasks);
        recyclerView.setAdapter(taskAdapter);

        /*
        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText( Today.this, "This is my Toast message!", Toast.LENGTH_LONG).show();
                editExistTask(tasks.get(position));
            }
        });

         */

        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                //Intent intent = new Intent(Today.this , EditTaskActivity.class);
                //startActivityForResult(intent, ADD_NOTE_REQUEST);
                editExistTask(task, tasks.indexOf(task));

            }
        });
    }

    public void Click(View view) {
        if(view == addNewTask){
            createNewTask();
        }

    }

    public void editExistTask(Task existTask, int position){

        final Dialog dialog = new Dialog(Today.this);
        //The user will be able to cancel the dialog by typing anywhere outside the dialog
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_edit_task);

        //Initializing the views of the dialog
        final EditText title = dialog.findViewById(R.id.ed_title);
        final EditText description = dialog.findViewById(R.id.ed_description);
        final TextView date = dialog.findViewById(R.id.ed_date);
        final ImageButton alarm = dialog.findViewById(R.id.ib_calender);
        final ImageButton priority = dialog.findViewById(R.id.ib_flag);
        final ImageButton delete = dialog.findViewById(R.id.ib_delete);
        final ImageButton submit = dialog.findViewById(R.id.ib_send);
        NumberPicker myNumberPicker = new NumberPicker(Today.this);

        title.setText(existTask.getTitle());
        description.setText(existTask.getDescription());
        date.setText(existTask.getDate());

        final String prioritySri = existTask.getPriority();
        Character priorityCh = prioritySri.charAt(prioritySri.length()-1);
        int priorityNum = Character.getNumericValue(priorityCh);
        myNumberPicker.setMaxValue(4);
        myNumberPicker.setMinValue(1);
        myNumberPicker.setValue(priorityNum);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasks.set(position, new Task(title.getText().toString(), description.getText().toString(), date.getText().toString(), "flag"+myNumberPicker.getValue(), "", false));
                taskAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");

                //tasks.add(new Task(title.getText().toString(), description.getText().toString(), date.getText().toString(), "", "", false));
                /*
                Newtask.setTitle(title.getText().toString().trim());
                Newtask.setDescription(description.getText().toString().trim());
                Newtask.setDate(currentDate);
                tasks.add(Newtask);

                 */
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                date.setText(currentDate);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Today.this).setView(myNumberPicker);
                builder.setTitle("Priority");
                builder.setPositiveButton("End", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    }
                });
                builder.show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(Today.this);
                deleteDialog.setTitle("Are you sure you'd like to delete this task?");
                deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        tasks.remove(position);
                        taskAdapter.notifyDataSetChanged();
                        dialog.dismiss();
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
    }

    public void createNewTask() {
        final Dialog dialog = new Dialog(Today.this);
        //The user will be able to cancel the dialog by typing anywhere outside the dialog
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_edit_task);

        //Initializing the views of the dialog
        final EditText title = dialog.findViewById(R.id.ed_title);
        final EditText description = dialog.findViewById(R.id.ed_description);
        final TextView date = dialog.findViewById(R.id.ed_date);
        final ImageButton alarm = dialog.findViewById(R.id.ib_calender);
        final ImageButton priority = dialog.findViewById(R.id.ib_flag);
        final ImageButton delete = dialog.findViewById(R.id.ib_delete);
        final ImageButton submit = dialog.findViewById(R.id.ib_send);
        NumberPicker myNumberPicker = new NumberPicker(Today.this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasks.add(new Task(title.getText().toString(), description.getText().toString(), date.getText().toString(), "flag"+(int)myNumberPicker.getValue(), "", false));
                taskAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
               // tasks.add(new Task(title.getText().toString(), description.getText().toString(), date.getText().toString(), "flag"+(int)myNumberPicker.getValue(), "", false));
                /*
                Newtask.setTitle(title.getText().toString().trim());
                Newtask.setDescription(description.getText().toString().trim());
                Newtask.setDate(currentDate);
                tasks.add(Newtask);

                 */
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                date.setText(currentDate);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Today.this).setView(myNumberPicker);
                builder.setTitle("Priority");
                builder.setPositiveButton("End", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    }
                });
                builder.show();
            }
        });

        delete.setVisibility(View.INVISIBLE);

        dialog.show();
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


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        startAlarm(c);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
    }

    private void startAlarm(Calendar c) {

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }
}
