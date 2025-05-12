package com.example.myproekt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = new DatabaseHelper(this);

        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText loginInput = findViewById(R.id.loginInput);
                EditText passwordInput = findViewById(R.id.passwordInput);
                String login = loginInput.getText().toString();
                String password = passwordInput.getText().toString();

                String role = dbHelper.checkUser(login, password);
                if (role != null) {
                    Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                            "SELECT id FROM users WHERE login = ?",
                            new String[]{login}
                    );
                    if (cursor.moveToFirst()) {
                        int userId = cursor.getInt(0);
                        // Сохраняем в SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        prefs.edit().putInt("current_user_id", userId).apply();
                    }
                    cursor.close();
                    Intent intent;
                    if (role.equals("manager")) {
                        intent = new Intent(LoginActivity.this, ManagerActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, ClientActivity.class);
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Ошибка входа!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}