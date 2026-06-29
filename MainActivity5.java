package com.example.sleephollow;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class MainActivity5 extends AppCompatActivity {

    private Spinner spinnerAwards;
    private TextView tvAwardInfo;
    private RequestQueue requestQueue;
    private String ssn;
    private boolean spinnerReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        spinnerAwards = findViewById(R.id.spinnerAwards);
        tvAwardInfo   = findViewById(R.id.tvAwardInfo);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(this);

        ssn = getIntent().getStringExtra("EMPLOYEE_SSN");
        Log.d("DEBUG_SSN", "MainActivity5 received SSN: '" + ssn + "'");

        loadAwards();

        spinnerAwards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (!spinnerReady) return;

                String awardId = parent.getItemAtPosition(position).toString().trim();
                if (!awardId.isEmpty()) {
                    getAwardDetails(awardId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadAwards() {
        String url = "http://10.0.2.2:8080/sleepyhollow/AwardIds.jsp?ssn=" + ssn;
        Log.d("DEBUG_URL", "AwardIds URL: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("DEBUG_RESPONSE", "AwardIds raw response: '" + response + "'");

                    List<String> awards = new ArrayList<>();
                    String[] rows = response.split("#");
                    for (String row : rows) {
                        if (!row.trim().isEmpty()) {
                            awards.add(row.trim());
                        }
                    }

                    if (awards.isEmpty()) {
                        tvAwardInfo.setText("No awards found.");
                        return;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            awards);

                    spinnerAwards.setAdapter(adapter);
                    spinnerReady = true;
                },
                error -> {
                    Log.e("DEBUG_ERROR", "AwardIds error: " + error.toString());
                    tvAwardInfo.setText("Error loading awards.");
                });

        requestQueue.add(request);
    }

    private void getAwardDetails(String awardId) {
        String url = "http://10.0.2.2:8080/sleepyhollow/GrantedDetails.jsp?awardid="
                + awardId + "&ssn=" + ssn;
        Log.d("DEBUG_URL", "GrantedDetails URL: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("DEBUG_RESPONSE", "GrantedDetails raw response: '" + response + "'");

                    if (response == null || response.trim().isEmpty()) {
                        tvAwardInfo.setText("No details found for this award.");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    String[] rows = response.split("#");

                    for (String row : rows) {
                        if (!row.trim().isEmpty()) {
                            String[] cols = row.split(",");
                            if (cols.length >= 2) {
                                sb.append(String.format("%-15s %-20s", cols[0].trim(), cols[1].trim()))
                                        .append("\n");
                            }
                        }
                    }

                    tvAwardInfo.setText(sb.length() > 0 ? sb.toString() : "No details found.");
                },
                error -> {
                    Log.e("DEBUG_ERROR", "GrantedDetails error: " + error.toString());
                    tvAwardInfo.setText("Error loading award details.");
                });

        requestQueue.add(request);
    }
}