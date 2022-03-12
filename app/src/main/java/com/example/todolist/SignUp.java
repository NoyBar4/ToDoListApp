package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class SignUp extends AppCompatActivity {

    EditText Name,Password,Email;
    Button Connect;
    ArrayList<Task> tasks;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Name = findViewById(R.id.name);
        Password = findViewById(R.id.password);
        Email = findViewById(R.id.email);
        Connect = findViewById(R.id.sign_up);

       tasks = new ArrayList<>();
       firebaseAuth = FirebaseAuth.getInstance();

        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
                //Intent intent = new Intent(SignUp.this, Today.class);
                //startActivity(intent);
            }
        });
    }

    public void saveName(View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("name");

        myRef.setValue(Name.getText().toString());
    }

    public void savePassword(View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("password");

        myRef.setValue(Password.getText().toString());
    }

    public void saveEmail(View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("email");

        myRef.setValue(Email.getText().toString());
    }

    public void saveTasks(View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tasks");

        myRef.setValue(tasks);
    }

    public void addUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").push();

        User user = new User(Name.getText().toString(), Password.getText().toString(), Email.getText().toString(), tasks);
        myRef.setValue(user);
    }

    private void createUser () {
        String name = Name.getText().toString();
        String password = Password.getText().toString();
        String email = Email.getText().toString();

        if(TextUtils.isEmpty(name)) {
            Name.setError("Enter Your Name");
            return;
        }
        else if(TextUtils.isEmpty(password)) {
            Password.setError("Enter Your Password");
            return;
        }
        else if(TextUtils.isEmpty(email)) {
            Email.setError("Enter Your Email");
            return;
        }
        else if(!isValidEmail(email)) {
            Email.setError("invalid email");
            return;
        }
        else if(!isValidPassword(password)) {
            Password.setError("password should contain a letter");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    /*
                    addUser();
                    Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUp.this, Home.class));
                    finish();
                     */
                    // Sign in success
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(Name.getText().toString()).build();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("profile").push();

                    User mUser = new User(Name.getText().toString(), Password.getText().toString(), Email.getText().toString(), tasks);
                    myRef.setValue(mUser);

                    startActivity(new Intent(SignUp.this, Home.class));
                    finish();
                }
                else
                {
                    Toast.makeText(SignUp.this, "Registration Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPassword(CharSequence target) {
        boolean isChar = false;
        String str = target.toString();
        for (int i = 0; i< str.length(); i++)
        {
            if(str.charAt(i) >= 'a' && str.charAt(i) <= 'z')
                isChar = true;
        }
        return isChar;
    }
}