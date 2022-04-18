package com.example.todolist;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.todolist.Dashboard.countD;
import static com.example.todolist.Home.TasksForToday;
import static com.example.todolist.Home.currentUser;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private static final String TAG = TaskAdapter.class.getSimpleName();

    private static ArrayList<Task> tasks;
    private static OnItemClickListener mListener;
    private int selectedPosition = -1;
    //int countD = 0;

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public TaskAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View Taskview = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item , parent , false);
        return new TaskViewHolder(Taskview, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.title.setText(currentTask.getTitle().toString());
        holder.description.setText(currentTask.getDescription());
        holder.date.setText(currentTask.getDate());
        holder.tags.setText(currentTask.getTags());
        //holder.tags.setText(currentTask.getTags());
        //holder.calender.setImageResource(R.drawable.calender);
        holder.flag.setImageResource(
                holder.description.getResources().getIdentifier(currentTask.getPriority(), "drawable" , holder.description.getContext().getPackageName()));

        //holder.tag.setImageResource(
        //        holder.description.getResources().getIdentifier(currentTask.getCalender(), "drawable" , holder.description.getContext().getPackageName()));
        holder.title.setChecked(position == selectedPosition);
        holder.title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    currentTask.setDone(true);
                    DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("tasks");
                    tasksRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren())
                            {
                                if(ds.child("title").getValue() == currentTask.getTitle() ){
                                    if(ds.child("description").getValue() == currentTask.getDescription()){
                                        //if(ds.child("done").getValue() == "true") {
                                            String id = ds.getKey();
                                            DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser).child("tasks").child(id).getRef();

                                        countD++;
                                        DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("profile").child("task counter");
                                        countRef.setValue(countD);
                                        Log.d("taskCount",String.valueOf(countD));

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 0.5s = 5000ms
                                                holder.title.setSelected(true);
                                                deleteRef.removeValue();
                                                tasks.clear();
                                                notifyDataSetChanged();
                                            }
                                        }, 500);

                                        //}
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //Log.d("current",String.valueOf(currentTask.isDone()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public CheckBox title;
        public TextView description, date, tags;
        public ImageView flag;
        public LinearLayout layout;

        public TaskViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.cb_title);
            description = itemView.findViewById(R.id.tv_description);
            date = itemView.findViewById(R.id.tv_date);
            flag = itemView.findViewById(R.id.iv_flag);
            tags = itemView.findViewById(R.id.tv_tags);
            layout = itemView.findViewById(R.id.task);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }

                     */
                    int position = getAdapterPosition();
                    if (mListener != null && position  != RecyclerView.NO_POSITION)
                    mListener.onItemClick(tasks.get(position));
                }
            });

        }
    }
}
