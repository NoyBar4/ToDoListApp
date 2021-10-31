package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> tasks;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
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
        //holder.calender.setImageResource(R.drawable.calendar3);
        holder.flag.setImageResource(
                holder.description.getResources().getIdentifier(currentTask.getPriority(), "drawable" , holder.description.getContext().getPackageName()));

        holder.calender.setImageResource(
                holder.description.getResources().getIdentifier(currentTask.getCalender(), "drawable" , holder.description.getContext().getPackageName()));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public CheckBox title;
        public TextView description, date;
        public ImageView calender, flag;

        public TaskViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.cb_title);
            description = itemView.findViewById(R.id.tv_description);
            date = itemView.findViewById(R.id.tv_date);
            calender = itemView.findViewById(R.id.iv_calender);
            flag = itemView.findViewById(R.id.iv_flag);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
