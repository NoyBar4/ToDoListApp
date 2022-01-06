package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LogIn extends AppCompatActivity {

    EditText Email,Password;
    Button Connect;
    TextView newAccount;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Connect = findViewById(R.id.log_in);
        newAccount = findViewById(R.id.signUp);

        firebaseAuth = FirebaseAuth.getInstance();

        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void logIn () {
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        if (email.isEmpty()) {
            Email.setError("Name can not be empty");
        }
        if (password.isEmpty()) {
            Password.setError("Password can not be empty");
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LogIn.this, Today.class));
                    } else {
                        Toast.makeText(LogIn.this, "Login Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
