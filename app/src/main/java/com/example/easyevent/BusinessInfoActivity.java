package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BusinessInfoActivity extends AppCompatActivity {

    private GetBusiness business;
    private String eventType, date, time, location;

    private TextView textViewName, textViewReviews, textViewServices;
    private Button buttonAddToCart, buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_info);

        textViewName = findViewById(R.id.text_view_name);
        textViewReviews = findViewById(R.id.text_view_reviews);
        textViewServices = findViewById(R.id.text_view_services);
        buttonAddToCart = findViewById(R.id.button_add_to_cart);
        buttonBack = findViewById(R.id.button_back);

        // Λήψη των δεδομένων από το intent
        business = (GetBusiness) getIntent().getSerializableExtra("BUSINESS");
        eventType = getIntent().getStringExtra("EVENT_TYPE");
        date = getIntent().getStringExtra("DATE");
        time = getIntent().getStringExtra("TIME");
        location = getIntent().getStringExtra("LOCATION");

        Log.d("BusinessInfoActivity", "Received business: " + business.getName());
        Log.d("BusinessInfoActivity", "Event Type: " + eventType);
        Log.d("BusinessInfoActivity", "Date: " + date);
        Log.d("BusinessInfoActivity", "Time: " + time);
        Log.d("BusinessInfoActivity", "Location: " + location);

        // Εμφάνιση των πληροφοριών της επιχείρησης
        textViewName.setText(business.getName());
        displayReviews();
        displayServices();

        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void displayReviews() {
        StringBuilder reviewsBuilder = new StringBuilder();
        if (business.getReviews() != null && !business.getReviews().isEmpty()) {
            for (Review review : business.getReviews()) {
                reviewsBuilder.append(review.getUser()).append(": ").append(review.getComment())
                        .append(" (Rating: ").append(review.getRating()).append(")").append("\n");
            }
        } else {
            reviewsBuilder.append("No reviews available.");
        }
        textViewReviews.setText(reviewsBuilder.toString());
    }

    private void displayServices() {
        StringBuilder servicesBuilder = new StringBuilder();
        if (business.getServices() != null && !business.getServices().isEmpty()) {
            for (String service : business.getServices()) {
                servicesBuilder.append(service).append("\n");
            }
        } else {
            servicesBuilder.append("No services available.");
        }
        textViewServices.setText(servicesBuilder.toString());
    }

    private void addToCart() {
        CartItem cartItem = new CartItem(business.getName(), eventType, date, time, location);
        Gson gson = new Gson();

        // Έλεγχος αν η υποβολή υπάρχει ήδη στο purchase.json
        if (isCartItemInPurchase(cartItem)) {
            showMessageAndRedirect();
            return;
        }

        List<CartItem> cartItems = new ArrayList<>();
        File file = new File(getFilesDir(), "easyevent.json");

        // Αν το αρχείο υπάρχει, φόρτωσε τα υπάρχοντα δεδομένα
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                Type listType = new TypeToken<List<CartItem>>() {}.getType();
                List<CartItem> loadedItems = gson.fromJson(jsonBuilder.toString(), listType);
                if (loadedItems != null) {
                    cartItems = loadedItems;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("BusinessInfoActivity", "Error reading from easyevent.json", e);
            }
        }

        // Προσθήκη του νέου αντικειμένου στη λίστα
        cartItems.add(cartItem);

        try (FileWriter fw = new FileWriter(file, false)) { // False για να αντικαταστήσουμε το αρχείο
            gson.toJson(cartItems, fw);
            fw.flush();
            Log.d("BusinessInfoActivity", "Data written to file successfully");

            // Επαλήθευση των γραφόμενων δεδομένων
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Log.d("BusinessInfoActivity", "Read from file: " + line);
                }
            }

            // Εμφάνιση μηνύματος Toast
            Toast.makeText(this, "Η επιχείρηση προστέθηκε στο καλάθι!", Toast.LENGTH_SHORT).show();

            // Επιστροφή στην προηγούμενη δραστηριότητα
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("BusinessInfoActivity", "Error writing to easyevent.json", e);
            // Εμφάνιση μηνύματος Toast για αποτυχία
            Toast.makeText(this, "Αποτυχία προσθήκης στο καλάθι. Παρακαλώ δοκιμάστε ξανά.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCartItemInPurchase(CartItem cartItem) {
        Gson gson = new Gson();
        List<CartItem> purchaseItems = new ArrayList<>();
        File file = new File(getFilesDir(), "purchase.json");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                Type listType = new TypeToken<List<CartItem>>() {}.getType();
                List<CartItem> loadedItems = gson.fromJson(jsonBuilder.toString(), listType);
                if (loadedItems != null) {
                    purchaseItems = loadedItems;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("BusinessInfoActivity", "Error reading from purchase.json", e);
            }
        }

        for (CartItem item : purchaseItems) {
            if (item.getBusinessName().equals(cartItem.getBusinessName())
                    && item.getDate().equals(cartItem.getDate())
                    && item.getEventType().equals(cartItem.getEventType())
                    && item.getLocation().equals(cartItem.getLocation())
                    && item.getTime().equals(cartItem.getTime())) {
                return true;
            }
        }
        return false;
    }

    private void showMessageAndRedirect() {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", "Η ημερομηνία και ώρα που υποβάλατε είναι κρατημένη, παρακαλώ δοκιμάστε άλλη ώρα ή ημερομηνία.");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
