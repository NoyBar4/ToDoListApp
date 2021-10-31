package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todolist.TaskAdapter.TaskViewHolder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.example.todolist.TaskAdapter.*;

public class Today extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {

    ArrayList<Task> tasks;
    Button addNewTask;
    Calendar c;
    //Task newTask = new Task("","", "","","",false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        addNewTask = findViewById(R.id.add_new_task);
        c = Calendar.getInstance();

        tasks = new ArrayList<>();
        for (int i = 0; i <10; i++){
            tasks.add(new Task("Task " + (i+1), "Description " + (i+1), "23rd Nov", "flag"+(i / 3 + 1), "calender"+(i % 2 + 1), false));
        }

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TaskAdapter taskAdapter = new TaskAdapter(tasks);
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText( Today.this, "This is my Toast message!", Toast.LENGTH_LONG).show();
               showCoustomDialog1(tasks.get(position));
            }
        });

    }

    public void Click(View view) {
        if(view == addNewTask){
            showCoustomDialog(new Task("","", "","","",false));
        }

    }

    public void showCoustomDialog1(Task existTask){
        final Dialog dialog = new Dialog(Today.this);
        //The user will be able to cancel the dialog by typing anywhere outside the dialog
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.task_dialog);

        //Initializing the views of the dialog
        final EditText title = dialog.findViewById(R.id.ed_title);
        final EditText description = dialog.findViewById(R.id.ed_description);
        final TextView date = dialog.findViewById(R.id.ed_date);
        final ImageButton alarm = dialog.findViewById(R.id.ib_calender);
        final ImageButton priority = dialog.findViewById(R.id.ib_flag);
        final ImageButton delete = dialog.findViewById(R.id.ib_delete);
        final ImageButton submit = dialog.findViewById(R.id.ib_send);

        //final CheckBox cv_title = dialog.findViewById(R.id.cb_title);

        title.setText(existTask.getTitle());
        description.setText(existTask.getDescription());
        date.setText(existTask.getDate());

        /*
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cv_title.setText(title.getText().toString());
                //existTask.setTitle(cv_title.getText().toString());
                String string = title.getText().toString().trim();
                Toast.makeText( Today.this, string, Toast.LENGTH_LONG).show();
                existTask.setTitle(string);
            }
        });

         */

        title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String string = v.getText().toString();
                    Toast.makeText( Today.this, string, Toast.LENGTH_LONG).show();
                    v.setText("");
                    existTask.setTitle(string);
                }
                return false;
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                existTask.setDescription(description.getText().toString());
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");

                existTask.setDate(date.getText().toString());
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        dialog.show();

    }

    public void showCoustomDialog(Task Newtask) {
        final Dialog dialog = new Dialog(Today.this);
        //The user will be able to cancel the dialog by typing anywhere outside the dialog
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.task_dialog);

        //Initializing the views of the dialog
        final EditText title = dialog.findViewById(R.id.ed_title);
        final EditText description = dialog.findViewById(R.id.ed_description);
        final TextView date = dialog.findViewById(R.id.ed_date);
        final ImageButton alarm = dialog.findViewById(R.id.ib_calender);
        final ImageButton priority = dialog.findViewById(R.id.ib_flag);
        final ImageButton delete = dialog.findViewById(R.id.ib_delete);
        final ImageButton submit = dialog.findViewById(R.id.ib_send);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tasks.add(Newtask);
                dialog.dismiss();
            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");

                Newtask.setTitle(title.getText().toString().trim());
                Newtask.setDescription(description.getText().toString().trim());
                Newtask.setDate(currentDate);
                tasks.add(Newtask);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

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

    String currentDate;
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