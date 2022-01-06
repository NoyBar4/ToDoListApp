package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

public class EditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "com.example.todolist.EXTRA_ID";
    public static final String EXTRA_TITLE ="com.example.todolist.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.todolist.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.todolist.EXTRA_PRIORITY";

    private EditText editTextTitle;
    private EditText editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        editTextTitle = findViewById(R.id.ed_title);
        editTextDescription = findViewById(R.id.ed_description);
        ImageButton submit = findViewById(R.id.ib_send);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.alarm);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_POSITION)) {
            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
        } else {
            setTitle("Add Note");
        }
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();


        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_LONG).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);

        setResult(RESULT_OK, data);
        finish();
    }
}