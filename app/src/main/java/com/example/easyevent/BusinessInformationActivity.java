package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class BusinessInformationActivity extends AppCompatActivity {

    private TextView txtBusinessName, txtBusinessType, txtEventTypes, txtLocation, txtContactInfo, txtCredential;
    private String businessName;
    private GetBusiness businessObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_information);

        txtBusinessName = findViewById(R.id.txtBusinessName);
        txtBusinessType = findViewById(R.id.txtBusinessType);
        txtEventTypes = findViewById(R.id.txtEventTypes);
        txtLocation = findViewById(R.id.txtLocation);
        txtContactInfo = findViewById(R.id.txtContactInfo);
        txtCredential = findViewById(R.id.txtCredential);

        Intent intent = getIntent();
        businessName = intent.getStringExtra("business");

        businessObject = getBusinessInfo(businessName);

        if (businessObject != null) {
            txtBusinessName.setText(businessObject.getName());
            txtBusinessType.setText(businessObject.getBusinessType());
            txtEventTypes.setText(businessObject.getEventTypes().toString());
            txtLocation.setText(businessObject.getLocation());
            txtContactInfo.setText(businessObject.getContactInfo());
            txtCredential.setText(businessObject.getCredential());
        } else {
            Toast.makeText(this, "Σφάλμα κατά την ανάγνωση των στοιχείων της επιχείρησης", Toast.LENGTH_SHORT).show();
        }
    }

    private GetBusiness getBusinessInfo(String businessName) {
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

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getString("name").equals(businessName)) {
                    GetBusiness business = new GetBusiness();
                    business.setName(obj.getString("name"));
                    business.setBusinessType(obj.getString("businessType"));
                    business.setEventTypes(jsonArrayToList(obj.getJSONArray("eventTypes")));
                    business.setLocation(obj.getString("location"));
                    business.setAvailableDates(jsonArrayToList(obj.getJSONArray("availableDates")));
                    business.setAvailableTimes(jsonArrayToList(obj.getJSONArray("availableTimes")));
                    business.setContactInfo(obj.getString("contactInfo"));
                    business.setState(obj.getBoolean("state"));
                    business.setCredential(obj.getString("credential"));

                    return business;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την ανάγνωση των δεδομένων", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void confirmBusiness(View view) {
        try {
            businessObject.setState(true);
            updateBusinessFile(businessObject);

            Toast.makeText(this, "Η επιχείρηση επιβεβαιώθηκε επιτυχώς", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AdministratorHomePageActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την επιβεβαίωση της επιχείρησης", Toast.LENGTH_SHORT).show();
        }
    }

    public void rejectBusiness(View view) {
        try {
            addToBlacklist(businessObject);
            removeFromBusinessFile(businessObject);

            Toast.makeText(this, "Η επιχείρηση απορρίφθηκε επιτυχώς", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AdministratorHomePageActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την απόρριψη της επιχείρησης", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBusinessFile(GetBusiness updatedBusiness) {
        try {
            File file = new File(getFilesDir(), "business.json");
            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getString("name").equals(updatedBusiness.getName())) {
                    JSONObject updatedObject = new JSONObject();
                    updatedObject.put("name", updatedBusiness.getName());
                    updatedObject.put("businessType", updatedBusiness.getBusinessType());
                    updatedObject.put("eventTypes", new JSONArray(updatedBusiness.getEventTypes()));
                    updatedObject.put("location", updatedBusiness.getLocation());
                    updatedObject.put("availableDates", new JSONArray(updatedBusiness.getAvailableDates()));
                    updatedObject.put("availableTimes", new JSONArray(updatedBusiness.getAvailableTimes()));
                    updatedObject.put("contactInfo", updatedBusiness.getContactInfo());
                    updatedObject.put("state", updatedBusiness.isState());
                    updatedObject.put("credential", updatedBusiness.getCredential());
                    // Αρχικοποίηση των reviews και services (προαιρετικά, αν υπάρχουν)
                    if (updatedBusiness.getReviews() != null) {
                        updatedObject.put("reviews", new JSONArray(updatedBusiness.getReviews()));
                    }
                    if (updatedBusiness.getServices() != null) {
                        updatedObject.put("services", new JSONArray(updatedBusiness.getServices()));
                    }
                    jsonArray.put(i, updatedObject);
                    break;
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(jsonArray.toString());
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την ενημέρωση του αρχείου", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToBlacklist(GetBusiness business) {
        try {
            File file = new File(getFilesDir(), "blacklist.json");
            JSONArray jsonArray;
            if (file.exists()) {
                InputStream is = new FileInputStream(file);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");
                jsonArray = new JSONArray(json);
            } else {
                jsonArray = new JSONArray();
            }

            JSONObject businessObject = new JSONObject();
            businessObject.put("name", business.getName());
            businessObject.put("businessType", business.getBusinessType());
            businessObject.put("eventTypes", new JSONArray(business.getEventTypes()));
            businessObject.put("location", business.getLocation());
            businessObject.put("availableDates", new JSONArray(business.getAvailableDates()));
            businessObject.put("availableTimes", new JSONArray(business.getAvailableTimes()));
            businessObject.put("contactInfo", business.getContactInfo());
            businessObject.put("state", business.isState());
            businessObject.put("credential", business.getCredential());
            // Αρχικοποίηση των reviews και services (προαιρετικά, αν υπάρχουν)
            if (business.getReviews() != null) {
                businessObject.put("reviews", new JSONArray(business.getReviews()));
            }
            if (business.getServices() != null) {
                businessObject.put("services", new JSONArray(business.getServices()));
            }

            jsonArray.put(businessObject);

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(jsonArray.toString());
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την προσθήκη στην blacklist", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromBusinessFile(GetBusiness business) {
        try {
            File file = new File(getFilesDir(), "business.json");
            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);

            JSONArray updatedArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (!obj.getString("name").equals(business.getName())) {
                    updatedArray.put(obj);
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(updatedArray.toString());
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την ενημέρωση του αρχείου", Toast.LENGTH_SHORT).show();
        }
    }
}
