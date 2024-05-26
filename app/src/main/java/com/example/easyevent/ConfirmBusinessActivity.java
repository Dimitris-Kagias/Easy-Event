package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ConfirmBusinessActivity extends AppCompatActivity {

    private LinearLayout layoutBusinesses;
    private static final String TAG = "ConfirmBusinessActivity";
    private static final int MESSAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_business);

        layoutBusinesses = findViewById(R.id.layoutBusinesses);

        ArrayList<String> businesses = getUnconfirmedBusinesses();

        if (businesses.isEmpty()) {
            Intent intent = new Intent(ConfirmBusinessActivity.this, MessageActivity.class);
            intent.putExtra("MESSAGE", "Δεν υπάρχουν επιχειρήσεις προς επιβεβαίωση");
            startActivityForResult(intent, MESSAGE_REQUEST_CODE);
        } else {
            for (String business : businesses) {
                Button btnBusiness = new Button(this);
                btnBusiness.setText(business);
                btnBusiness.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ConfirmBusinessActivity.this, BusinessInformationActivity.class);
                        intent.putExtra("business", business);
                        startActivity(intent);
                    }
                });
                layoutBusinesses.addView(btnBusiness);
            }
        }
    }

    private ArrayList<String> getUnconfirmedBusinesses() {
        ArrayList<String> businesses = new ArrayList<>();
        try {
            File file = new File(getFilesDir(), "business.json");
            if (!file.exists()) {
                throw new Exception("Το αρχείο business.json δεν βρέθηκε.");
            }

            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            Log.d(TAG, "JSON data: " + json);

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (!obj.getBoolean("state")) {
                    businesses.add(obj.getString("name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την ανάγνωση των δεδομένων", Toast.LENGTH_SHORT).show();
        }
        return businesses;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MESSAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, AdministratorHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
