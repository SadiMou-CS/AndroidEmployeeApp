package com.example.sleephollow;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.widget.Button;

public class MainActivity3 extends AppCompatActivity {

    private TextView tvTransactions;
    private RequestQueue requestQueue;
    private String employeeSsn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        tvTransactions = findViewById(R.id.tvTransactions);
        requestQueue = Volley.newRequestQueue(this);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        employeeSsn = getIntent().getStringExtra("EMPLOYEE_SSN");
        if (employeeSsn != null) {
            fetchTransactions(employeeSsn);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchTransactions(String ssn) {
        String url = "http://10.0.2.2:8080/sleepyhollow/Transactions.jsp?ssn=" + ssn;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    String data = response.trim();

                    if (data.isEmpty() || data.contains("Error")) {
                        tvTransactions.setText("No transactions found.");
                    } else {
                        StringBuilder formattedData = new StringBuilder();
                        formattedData.append("ID            Date            Amount\n");
                        formattedData.append("_______________________________________\n");

                        String[] rows = data.split("#");
                        for (String row : rows) {
                            String[] cols = row.split(",");
                            if (cols.length >= 3) {
                                formattedData.append(String.format("%-10s %-16s %s\n", cols[0], cols[1], "   $" + cols[2]));
                            }
                        }
                        tvTransactions.setText(formattedData.toString());
                    }
                },
                error -> {
                    tvTransactions.setText("Failed to connect to server.");
                    Toast.makeText(MainActivity3.this, "Network Error", Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(stringRequest);
    }
}