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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventsActivity extends AppCompatActivity {

    private LinearLayout layoutEvents;
    private static final String TAG = "EventsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        layoutEvents = findViewById(R.id.layoutEvents);

        ArrayList<String> completedEvents = getCompletedEvents();

        if (completedEvents.isEmpty()) {
            showMessage("Δεν έχετε πραγματοποιήσει κάποια εκδήλωση");
        } else {
            for (String event : completedEvents) {
                Button btnEvent = new Button(this);
                btnEvent.setText(event);
                btnEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventsActivity.this, EventBusinessesActivity.class);
                        intent.putExtra("event", event);
                        startActivity(intent);
                    }
                });
                layoutEvents.addView(btnEvent);
            }
        }
    }

    private ArrayList<String> getCompletedEvents() {
        ArrayList<String> completedEvents = new ArrayList<>();
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

            Log.d(TAG, "JSON data: " + json);

            JSONArray jsonArray = new JSONArray(json);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Log.d(TAG, "Current date: " + currentDate);

            SimpleDateFormat jsonDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat requiredDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (!obj.has("eventType") || !obj.has("date")) {
                    Log.d(TAG, "Skipping object due to missing fields: " + obj.toString());
                    continue;
                }

                String eventDate = obj.getString("date");
                String eventName = obj.getString("eventType");

                // Μετατροπή ημερομηνίας από τη μορφή dd/MM/yyyy στη μορφή yyyy-MM-dd
                Date parsedEventDate = jsonDateFormat.parse(eventDate);
                String formattedEventDate = requiredDateFormat.format(parsedEventDate);

                Log.d(TAG, "Checking event: " + eventName + " with date: " + formattedEventDate);

                if (formattedEventDate.compareTo(currentDate) < 0) {
                    completedEvents.add(eventName);
                    Log.d(TAG, "Added completed event: " + eventName);
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date", e);
            Toast.makeText(this, "Σφάλμα κατά την ανάγνωση της ημερομηνίας", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την ανάγνωση των δεδομένων", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error reading JSON data", e);
        }
        return completedEvents;
    }

    private void showMessage(String message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", message);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Intent intent = new Intent(this, CustomerHomePageActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
