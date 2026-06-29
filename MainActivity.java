package com.example.sleephollow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        requestQueue = Volley.newRequestQueue(this);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                authenticateUser(username, password);
            }
        });
    }

    private void authenticateUser(String username, String password) {
        String url = "http://10.0.2.2:8080/sleepyhollow/login?user=" + username + "&pass=" + password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    String result = response.trim();

                    if (result.startsWith("Yes:")) {
                        String ssn = result.split(":")[1];
                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        intent.putExtra("EMPLOYEE_SSN", ssn);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "Server Connection Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        requestQueue.add(stringRequest);
    }
}