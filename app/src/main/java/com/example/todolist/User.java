package com.example.todolist;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class User {

    private String Name, Password, Email;
    private ArrayList<Task> Tasks;

    public User(String name, String password, String email, ArrayList<Task> tasks) {
        this.Name = name;
        this.Password = password;
        this.Email = email;
        this.Tasks = new ArrayList<Task> ();
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }

    public String getEmail() {
        return Email;
    }

    public ArrayList<Task> getTasks() {
        return Tasks;
    }
}
