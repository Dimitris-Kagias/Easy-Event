package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class EventBusinessesActivity extends AppCompatActivity {

    private LinearLayout layoutBusinesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_businesses);

        layoutBusinesses = findViewById(R.id.layoutBusinesses);

        Intent intent = getIntent();
        String eventName = intent.getStringExtra("event");

        ArrayList<String> businesses = getEventBusinesses(eventName);

        for (String business : businesses) {
            Button btnBusiness = new Button(this);
            btnBusiness.setText(business);
            btnBusiness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventBusinessesActivity.this, AddRatingActivity.class);
                    intent.putExtra("business", business);
                    startActivity(intent);
                }
            });
            layoutBusinesses.addView(btnBusiness);
        }
    }

    private ArrayList<String> getEventBusinesses(String eventName) {
        ArrayList<String> businesses = new ArrayList<>();
        try {
            File file = new File(getFilesDir(), "purchase.json");
            if (!file.exists()) {
                throw new Exception("Το αρχείο purchase.json δεν βρέθηκε.");
            }

            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getString("eventType").equals(eventName)) {
                    businesses.add(obj.getString("businessName"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την ανάγνωση των δεδομένων", Toast.LENGTH_SHORT).show();
        }
        return businesses;
    }
}
