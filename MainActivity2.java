package com.example.sleephollow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

public class MainActivity2 extends AppCompatActivity {

    private TextView tvWelcomeName, tvTotalSales;
    private ImageView ivProfilePicture;
    private Button btnTransactions, btnTransDetails, btnAwardDetails, btnTransfer, btnExit;
    private RequestQueue requestQueue;
    private String employeeSsn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvTotalSales  = findViewById(R.id.tvTotalSales);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        btnTransactions = findViewById(R.id.btnTransactions);
        btnTransDetails = findViewById(R.id.btnTransDetails);
        btnAwardDetails = findViewById(R.id.btnAwardDetails);
        btnTransfer     = findViewById(R.id.btnTransfer);
        btnExit         = findViewById(R.id.btnExit);

        requestQueue = Volley.newRequestQueue(this);
        employeeSsn  = getIntent().getStringExtra("EMPLOYEE_SSN");

        loadProfileImage(employeeSsn);

        btnTransactions.setOnClickListener(v -> navigateTo(MainActivity3.class));
        btnTransDetails.setOnClickListener(v -> navigateTo(MainActivity4.class));
        btnAwardDetails.setOnClickListener(v -> navigateTo(MainActivity5.class));
        btnTransfer.setOnClickListener(v -> navigateTo(MainActivity6.class));
        btnExit.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (employeeSsn != null) {
            fetchEmployeeInfo(employeeSsn);
        }
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(MainActivity2.this, targetActivity);
        intent.putExtra("EMPLOYEE_SSN", employeeSsn);
        startActivity(intent);
    }

    private void fetchEmployeeInfo(String ssn) {
        String url = "http://10.0.2.2:8080/sleepyhollow/Info.jsp?ssn=" + ssn;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    String[] parts = response.trim().split(",");
                    if (parts.length >= 2) {
                        tvWelcomeName.setText(parts[0]);
                        tvTotalSales.setText("$" + parts[1]);
                    } else {
                        tvWelcomeName.setText("Employee not found");
                    }
                },
                error -> {
                    tvWelcomeName.setText("Connection Failed");
                    Toast.makeText(MainActivity2.this, "Network Error", Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(stringRequest);
    }

    private void loadProfileImage(String ssn) {
        String imageUrl = "http://10.0.2.2:8080/sleepyhollow/images/" + ssn + ".jpeg";
        Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivProfilePicture);
    }
}