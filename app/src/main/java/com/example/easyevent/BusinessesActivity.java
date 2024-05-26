package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BusinessesActivity extends AppCompatActivity {

    private LinearLayout businessButtonLayout;
    private List<GetBusiness> allBusinesses;
    private List<String> filteredBusinessNames;
    private String eventType, date, time, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businesses);

        businessButtonLayout = findViewById(R.id.business_button_layout);

        // Λήψη των δεδομένων από το intent
        Intent intent = getIntent();
        filteredBusinessNames = intent.getStringArrayListExtra("BUSINESS_NAMES");
        eventType = intent.getStringExtra("EVENT_TYPE");
        date = intent.getStringExtra("DATE");
        time = intent.getStringExtra("TIME");
        location = intent.getStringExtra("LOCATION");
        Log.d("BusinessesActivity", "Received business names: " + filteredBusinessNames);

        // Φόρτωση των δεδομένων των επιχειρήσεων από το αρχείο JSON
        loadBusinessesFromJson();

        // Προσθήκη κουμπιών για τις επιχειρήσεις
        addBusinessButtons();
    }

    private void loadBusinessesFromJson() {
        String json = null;
        try {
            InputStream is = getAssets().open("business.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (json != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<GetBusiness>>() {}.getType();
            allBusinesses = gson.fromJson(json, listType);
            Log.d("BusinessesActivity", "Loaded businesses from JSON: " + allBusinesses);
        } else {
            allBusinesses = new ArrayList<>();
            Log.d("BusinessesActivity", "No businesses found in JSON.");
        }
    }

    private void addBusinessButtons() {
        if (filteredBusinessNames != null && !filteredBusinessNames.isEmpty()) {
            for (String businessName : filteredBusinessNames) {
                for (GetBusiness business : allBusinesses) {
                    if (business.getName().equals(businessName)) {
                        Button button = new Button(this);
                        button.setText(businessName);
                        button.setOnClickListener(v -> openBusinessInfo(business));
                        businessButtonLayout.addView(button);
                        break;
                    }
                }
            }
        }
    }

    private void openBusinessInfo(GetBusiness business) {
        Intent intent = new Intent(this, BusinessInfoActivity.class);
        intent.putExtra("BUSINESS", business);
        intent.putExtra("EVENT_TYPE", eventType);
        intent.putExtra("DATE", date);
        intent.putExtra("TIME", time);
        intent.putExtra("LOCATION", location);
        Log.d("BusinessesActivity", "Opening business info for: " + business.getName());
        startActivity(intent);
    }
}
