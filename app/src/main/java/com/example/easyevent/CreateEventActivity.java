package com.example.easyevent;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CreateEventActivity extends AppCompatActivity {

    private Spinner spinnerEventType;
    private EditText editTextDate;
    private Spinner spinnerTime;
    private Spinner spinnerAmPm;
    private EditText editTextLocation;

    private List<GetBusiness> businesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        spinnerEventType = findViewById(R.id.spinner_event_type);
        editTextDate = findViewById(R.id.editText_date);
        spinnerTime = findViewById(R.id.spinner_time);
        spinnerAmPm = findViewById(R.id.spinner_am_pm);
        editTextLocation = findViewById(R.id.editText_location);

        // Προσθήκη επιλογών στα Spinners
        ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.event_types_array, android.R.layout.simple_spinner_item);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(eventTypeAdapter);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeAdapter);

        ArrayAdapter<CharSequence> amPmAdapter = ArrayAdapter.createFromResource(this,
                R.array.am_pm_array, android.R.layout.simple_spinner_item);
        amPmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAmPm.setAdapter(amPmAdapter);

        // Ορισμός click listener για το EditText της ημερομηνίας
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Φόρτωση των δεδομένων των επιχειρήσεων από το αρχείο JSON
        loadBusinessesFromJson();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editTextDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);

        datePickerDialog.show();
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
            List<GetBusiness> allBusinesses = gson.fromJson(json, listType);
            businesses = allBusinesses.stream()
                    .filter(GetBusiness::isState)
                    .collect(Collectors.toList());
            Log.d("CreateEventActivity", "Loaded businesses: " + businesses.toString());
        } else {
            businesses = new ArrayList<>();
        }
    }

    public void submitEvent(View view) {
        String eventType = spinnerEventType.getSelectedItem().toString();
        String date = editTextDate.getText().toString();
        String time = spinnerTime.getSelectedItem().toString() + " " + spinnerAmPm.getSelectedItem().toString();
        String location = editTextLocation.getText().toString();

        if (eventType.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
            showMessage("Παρακαλώ συμπληρώστε όλα τα πεδία.");
        } else {
            List<String> businessTypes = findMatchingBusinesses(eventType, date, time, location);

            if (businessTypes.isEmpty()) {
                showMessage("Δεν βρέθηκαν επιχειρήσεις που να πληρούν τα κριτήρια.");
            } else {
                Intent intent = new Intent(this, BusinessTypeActivity.class);
                intent.putStringArrayListExtra("BUSINESS_TYPES", new ArrayList<>(businessTypes));
                intent.putExtra("EVENT_TYPE", eventType);
                intent.putExtra("DATE", date);
                intent.putExtra("TIME", time);
                intent.putExtra("LOCATION", location);
                startActivity(intent);
            }
        }
    }

    private List<String> findMatchingBusinesses(String eventType, String date, String time, String location) {
        if (businesses == null) {
            return new ArrayList<>();
        }

        final String finalFormattedDate = formatDate(date);
        final String finalTime = formatTime(time.trim());

        return businesses.stream()
                .filter(business -> business.getEventTypes().contains(eventType))
                .filter(business -> business.getLocation().equalsIgnoreCase(location))
                .filter(business -> business.getAvailableDates().contains(finalFormattedDate))
                .filter(business -> business.getAvailableTimes().contains(finalTime))
                .map(GetBusiness::getBusinessType)
                .distinct()
                .collect(Collectors.toList());
    }

    private String formatDate(String date) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = inputDateFormat.parse(date);
            return jsonDateFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String formatTime(String time) {
        if (time.matches("^[1-9]:\\d{2} [APM]{2}$")) {
            return "0" + time;
        }
        return time;
    }

    private void showMessage(String message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", message);
        startActivity(intent);
    }
}
