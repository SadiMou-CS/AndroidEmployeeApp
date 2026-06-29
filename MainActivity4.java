package com.example.sleephollow;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
import java.util.List;

public class MainActivity4 extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Spinner spinner;
    private TextView tvDetails;
    private TextView tvHeaderInfo;
    private boolean spinnerReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        spinner      = findViewById(R.id.spinnerTxn);
        tvDetails    = findViewById(R.id.tvDetails);
        tvHeaderInfo = findViewById(R.id.tvHeaderInfo);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(this);

        String ssn = getIntent().getStringExtra("EMPLOYEE_SSN");
        Log.d("DEBUG_SSN", "MainActivity4 received SSN: '" + ssn + "'");

        String url = "http://10.0.2.2:8080/sleepyhollow/Transactions.jsp?ssn=" + ssn;
        Log.d("DEBUG_URL", "Transactions URL: " + url);

        StringRequest listRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("DEBUG_RESPONSE", "Transactions raw response: '" + response + "'");

                    List<String> txnIds = new ArrayList<>();
                    String[] rows = response.split("#");
                    for (String row : rows) {
                        if (!row.trim().isEmpty()) {
                            String[] cols = row.split(",");
                            if (cols.length > 0 && !cols[0].trim().isEmpty()) {
                                txnIds.add(cols[0].trim());
                            }
                        }
                    }

                    if (txnIds.isEmpty()) {
                        tvDetails.setText("No transactions found.");
                        return;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            txnIds);

                    spinner.setAdapter(adapter);
                    spinnerReady = true;
                },
                error -> {
                    Log.e("DEBUG_ERROR", "Transactions error: " + error.toString());
                    tvDetails.setText("Error loading transactions.");
                });

        requestQueue.add(listRequest);

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent,
                                       android.view.View view,
                                       int position, long id) {
                if (!spinnerReady) return;

                String selectedId = parent.getItemAtPosition(position).toString().trim();

                if (selectedId.matches("\\d+")) {
                    fetchTransactionDetails(selectedId);
                } else {
                    Log.e("MainActivity4", "Invalid ID format: " + selectedId);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void fetchTransactionDetails(String transactionId) {
        String url = "http://10.0.2.2:8080/sleepyhollow/TransactionDetails.jsp?txnid=" + transactionId;
        Log.d("DEBUG_URL", "TransactionDetails URL: " + url);

        StringRequest detailRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("DEBUG_RESPONSE", "TransactionDetails raw response: '" + response + "'");

                    if (response == null || response.trim().isEmpty()) {
                        tvDetails.setText("No details found for this transaction.");
                        return;
                    }

                    String[] rows = response.split("#");

                    String[] firstCols = rows[0].split(",");
                    if (firstCols.length >= 2) {
                        tvHeaderInfo.setText(firstCols[0].trim() + "              $" + firstCols[1].trim());
                    }

                    StringBuilder sb = new StringBuilder();
                    for (String row : rows) {
                        if (!row.trim().isEmpty()) {
                            String[] cols = row.split(",");
                            if (cols.length >= 5) {
                                sb.append(String.format("%-15s %-9s %-5s",
                                                cols[2].trim(),   
                                                cols[3].trim(),   
                                                cols[4].trim()))  
                                        .append("\n");
                            }
                        }
                    }

                    tvDetails.setText(sb.length() > 0 ? sb.toString() : "No details found.");
                },
                error -> {
                    Log.e("DEBUG_ERROR", "TransactionDetails error: " + error.toString());
                    tvDetails.setText("Error loading transaction details.");
                });

        requestQueue.add(detailRequest);
    }
}