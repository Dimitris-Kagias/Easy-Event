package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BusinessTypeActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private List<GetBusiness> businesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_type);

        gridLayout = findViewById(R.id.grid_layout);

        // Φόρτωση των δεδομένων των επιχειρήσεων από το αρχείο JSON
        loadBusinessesFromJson();

        ArrayList<String> businessTypes = getIntent().getStringArrayListExtra("BUSINESS_TYPES");
        String eventType = getIntent().getStringExtra("EVENT_TYPE");
        String date = getIntent().getStringExtra("DATE");
        String time = getIntent().getStringExtra("TIME");
        String location = getIntent().getStringExtra("LOCATION");
        Log.d("BusinessTypeActivity", "Received business types: " + businessTypes);

        if (businessTypes != null) {
            for (String type : businessTypes) {
                Log.d("BusinessTypeActivity", "Adding button for business type: " + type);
                Button button = new Button(this);
                button.setText(type);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("BusinessTypeActivity", "Button clicked for business type: " + type);
                        // Φιλτράρισμα των επιχειρήσεων βάσει του τύπου
                        List<String> filteredBusinessNames = filterBusinessesByType(type);
                        Log.d("BusinessTypeActivity", "Filtered business names: " + filteredBusinessNames);
                        if (!filteredBusinessNames.isEmpty()) {
                            Intent intent = new Intent(BusinessTypeActivity.this, BusinessesActivity.class);
                            intent.putStringArrayListExtra("BUSINESS_NAMES", new ArrayList<>(filteredBusinessNames));
                            intent.putExtra("EVENT_TYPE", eventType);
                            intent.putExtra("DATE", date);
                            intent.putExtra("TIME", time);
                            intent.putExtra("LOCATION", location);
                            startActivity(intent);
                        } else {
                            // Εμφάνιση μηνύματος αν δεν βρεθούν επιχειρήσεις
                            showMessage("Δεν βρέθηκαν επιχειρήσεις με τον επιλεγμένο τύπο.");
                        }
                    }
                });
                gridLayout.addView(button);
            }
        }
    }

    // Φόρτωση των επιχειρήσεων από το JSON αρχείο
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
            businesses = gson.fromJson(json, listType);
            Log.d("BusinessTypeActivity", "Loaded businesses: " + businesses);
        } else {
            businesses = new ArrayList<>();
        }
    }

    // Εφαρμογή φιλτραρίσματος των επιχειρήσεων βάσει του τύπου
    private List<String> filterBusinessesByType(String type) {
        List<String> filteredBusinessNames = new ArrayList<>();
        if (businesses != null) {
            for (GetBusiness business : businesses) {
                if (business.getBusinessType().equalsIgnoreCase(type)) {
                    filteredBusinessNames.add(business.getName());
                }
            }
        }
        return filteredBusinessNames;
    }

    // Εμφάνιση μηνύματος
    private void showMessage(String message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", message);
        startActivity(intent);
    }
}
