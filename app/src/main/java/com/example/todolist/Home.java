package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Home extends AppCompatActivity {

    private static final String CHANNEL_ID = "10001";

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addNewTask;
    TextView Title, TasksForToday;

    Calendar c;
    static ArrayList<Task> tasks = new ArrayList<>();
    static ArrayList<Task> delitedTasks = new ArrayList<>();
    ArrayList<Tag> tags = new ArrayList<>();
    String currentDate;
    String[] tagArray = {"home", "work", "health", "study", "shopping"};

    RecyclerView recyclerViewTasks, recyclerViewTags;
    TaskAdapter taskAdapter;
    TagAdapter tagAdapter;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //createNotificationChannel();
        //Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.HOUR_OF_DAY, 17);
        //calendar.set(Calendar.MINUTE, 51);
        //calendar.set(Calendar.SECOND, 0);
        //calendar.set(Calendar.MILLISECOND,0);
        //startAlarm(calendar);

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

                    case R.id.miHome:
                        return true;
                }
                return true;
            }
        });

        c = Calendar.getInstance();
        currentDate = sdf.format(c.getTime());

        Title = findViewById(R.id.title);
        TasksForToday = findViewById(R.id.task_for_today);
        addNewTask = findViewById(R.id.fab);
        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTask();
            }
        });

        /*
        for (int i = 0; i <5; i++){
            tasks.add(new Task("Task " + (i+1), "Description " + (i+1), "today", "flag"+(i / 3 + 1), "calender", "Tags: ", "tag", false));
        }
          */
        TasksForToday.setText(String.format("you have %d tasks for today", tasks.size()));

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
                if (task.isDone())
                    tasks.remove(tasks.indexOf(task));
                else
                    editExistTask(task, tasks.indexOf(task));

            }
        });

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

        final ArrayList<Integer>[] tagList = new ArrayList[]{new ArrayList<>()};
        boolean[] selectedTag = new boolean[tagArray.length];

        Task newTask = new Task("", "", "", "", "flag4", "calender", tag.getText().toString(), "tag", false);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tasks.add(new Task(title.getText().toString(), description.getText().toString(), date.getText().toString(), "flag"+(int)myNumberPicker.getValue(), "calender", tag.getText().toString(), "tag", false));
                newTask.setTitle(title.getText().toString());
                newTask.setDescription(description.getText().toString());
                newTask.setTags(tag.getText().toString());

                //add to recycelView
                if (date.getText().toString().equals(currentDate))
                    tasks.add(newTask);

                Calender.allTasks.add(newTask);

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

                        }
                    };

                    timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourSelector, int minuteSelector) {
                            hour = hourSelector;
                            minute = minuteSelector;
                            alarm.setText(String.format(Locale.getDefault(), "%02d:%02d"));
                            newTask.setTime(alarm.getText().toString());
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
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String final_date = day + " / " + month + " / " + year;
                date.setText(final_date);
                newTask.setDate(date.getText().toString());
            }
        };

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
                        newTask.setTagsList(stringBuilder);
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
                tasks.set(position, newTask);
                if (!date.getText().toString().equals(currentDate))
                    tasks.remove(position);

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

                        }
                    };

                    timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourSelector, int minuteSelector) {
                            hour = hourSelector;
                            minute = minuteSelector;
                            alarm.setText(String.format(Locale.getDefault(), "%02d:%02d"));
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
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String final_date = day + " / " + month + " / " + year;
                date.setText(final_date);
            }
        };

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
                        existTask.setTagsList(stringBuilder);
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
                        tasks.remove(position);
                        delitedTasks.add(existTask);
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

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Home.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
}
