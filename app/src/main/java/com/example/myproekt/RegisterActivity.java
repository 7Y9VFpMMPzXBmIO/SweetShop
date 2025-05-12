package com.example.myproekt;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbHelper = new DatabaseHelper(this);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameInput = findViewById(R.id.nameInput);
                EditText phoneInput = findViewById(R.id.phoneInput);
                EditText loginInput = findViewById(R.id.loginInput);
                EditText passwordInput = findViewById(R.id.passwordInput);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", nameInput.getText().toString());
                values.put("phone", phoneInput.getText().toString());
                values.put("login", loginInput.getText().toString());
                values.put("password", passwordInput.getText().toString());
                values.put("role_id", 2); // Роль "client"

                try {
                    db.insertOrThrow("users", null, values);
                    Toast.makeText(RegisterActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, "Ошибка: логин занят", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


//