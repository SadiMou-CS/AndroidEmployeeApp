package com.example.sleephollow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity6 extends AppCompatActivity {

    EditText etDestination;
    EditText etAmount;
    Button btnTransfer;
    TextView tvResult;
    RequestQueue requestQueue;
    String sourceSSN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        etDestination = findViewById(R.id.etDestination);
        etAmount      = findViewById(R.id.etAmount);
        btnTransfer   = findViewById(R.id.btnTransfer);
        tvResult      = findViewById(R.id.tvResult);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(this);

        sourceSSN = getIntent().getStringExtra("EMPLOYEE_SSN");

        btnTransfer.setOnClickListener(v -> validateAndTransfer());
    }

    private void validateAndTransfer() {
        String destination = etDestination.getText().toString().trim();
        String amountStr   = etAmount.getText().toString().trim();

        
        if (sourceSSN == null || sourceSSN.isEmpty()) {
            tvResult.setText("Error: your SSN is missing. Please log in again.");
            return;
        }
        if (destination.isEmpty()) {
            tvResult.setText("Please enter a destination SSN.");
            return;
        }
        if (amountStr.isEmpty()) {
            tvResult.setText("Please enter an amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                tvResult.setText("Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            tvResult.setText("Please enter a valid number for amount.");
            return;
        }

       
        String destInfoUrl = "http://10.0.2.2:8080/sleepyhollow/Info.jsp?ssn=" + destination;

        StringRequest destCheckRequest = new StringRequest(Request.Method.GET, destInfoUrl,
                response -> {
                    String[] parts = response.trim().split(",");
                    if (parts.length < 2 || parts[0].trim().isEmpty()) {
                        tvResult.setText("Employee does not exist.");
                        return;
                    }

                    checkBalanceAndTransfer(destination, amountStr, amount);
                },
                error -> tvResult.setText("Employee does not exist.")
        );

        requestQueue.add(destCheckRequest);
    }

    private void checkBalanceAndTransfer(String destination, String amountStr, double amount) {
        String infoUrl = "http://10.0.2.2:8080/sleepyhollow/Info.jsp?ssn=" + sourceSSN;

        StringRequest balanceRequest = new StringRequest(Request.Method.GET, infoUrl,
                response -> {
                    String[] parts = response.trim().split(",");
                    if (parts.length < 2) {
                        tvResult.setText("Error: could not retrieve your balance.");
                        return;
                    }

                    double currentBalance;
                    try {
                        currentBalance = Double.parseDouble(parts[1].trim());
                    } catch (NumberFormatException e) {
                        tvResult.setText("Error: could not parse your balance.");
                        return;
                    }

                    if (amount > currentBalance) {
                        tvResult.setText("Insufficient funds. Your current balance is $" + parts[1].trim());
                        return;
                    }

                    transferFunds(destination, amountStr);
                },
                error -> tvResult.setText("Error checking balance: " + error.toString())
        );

        requestQueue.add(balanceRequest);
    }

    private void transferFunds(String destination, String amount) {
        String url = "http://10.0.2.2:8080/sleepyhollow/Transfer.jsp?ssn1="
                + sourceSSN
                + "&ssn2=" + destination
                + "&amount=" + amount;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> tvResult.setText(response),
                error -> tvResult.setText("Transfer failed: " + error.toString())
        );

        requestQueue.add(request);
    }
}