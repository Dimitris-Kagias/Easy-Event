package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Προσθήκη αυτού του import
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MESSAGE = 1;
    private CartItem cartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        cartItem = (CartItem) getIntent().getSerializableExtra("CART_ITEM");
        if (cartItem == null) {
            Log.e("PaymentActivity", "Received cartItem is null");
        } else {
            Log.d("PaymentActivity", "Received cartItem: " + cartItem.getBusinessName());
        }

        Button paymentButton = findViewById(R.id.button_payment);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBuying();
            }
        });
    }

    private void finishBuying() {
        // Μεταφορά των δεδομένων στο αρχείο purchase.json και διαγραφή από το easyevent.json
        transferCartItemToPurchase(cartItem);

        // Εμφάνιση μηνύματος επιτυχίας
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", "Επιτυχής ολοκλήρωση συναλλαγής");
        startActivityForResult(intent, REQUEST_CODE_MESSAGE);
    }

    private void transferCartItemToPurchase(CartItem cartItem) {
        if (cartItem == null) {
            Log.e("PaymentActivity", "cartItem is null, cannot proceed with transfer");
            return;
        }

        Gson gson = new Gson();
        List<CartItem> cartItems = new ArrayList<>();
        File easyeventFile = new File(getFilesDir(), "easyevent.json");
        File purchaseFile = new File(getFilesDir(), "purchase.json");

        // Ανάγνωση του easyevent.json και αφαίρεση του cartItem
        if (easyeventFile.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(easyeventFile)))) {
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
                Log.e("PaymentActivity", "Error reading from easyevent.json", e);
            }
        }

        // Αφαίρεση του cartItem από τη λίστα
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item == null || item.getBusinessName() == null) {
                iterator.remove();
            } else if (item.getBusinessName().equals(cartItem.getBusinessName())
                    && item.getDate().equals(cartItem.getDate())
                    && item.getEventType().equals(cartItem.getEventType())
                    && item.getLocation().equals(cartItem.getLocation())
                    && item.getTime().equals(cartItem.getTime())) {
                iterator.remove();
            }
        }

        // Εγγραφή της ενημερωμένης λίστας πίσω στο easyevent.json
        try (FileWriter fw = new FileWriter(easyeventFile, false)) {
            gson.toJson(cartItems, fw);
            fw.flush();
            Log.d("PaymentActivity", "Updated easyevent.json successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PaymentActivity", "Error writing to easyevent.json", e);
        }

        // Προσθήκη του cartItem στο purchase.json
        List<CartItem> purchaseItems = new ArrayList<>();
        if (purchaseFile.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(purchaseFile)))) {
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
                Log.e("PaymentActivity", "Error reading from purchase.json", e);
            }
        }

        // Αφαίρεση τυχόν null αντικειμένων από τη λίστα
        Iterator<CartItem> purchaseIterator = purchaseItems.iterator();
        while (purchaseIterator.hasNext()) {
            CartItem item = purchaseIterator.next();
            if (item == null || item.getBusinessName() == null) {
                purchaseIterator.remove();
            }
        }

        // Προσθήκη του νέου αντικειμένου στη λίστα
        purchaseItems.add(cartItem);

        // Εγγραφή της ενημερωμένης λίστας στο purchase.json
        try (FileWriter fw = new FileWriter(purchaseFile, false)) {
            gson.toJson(purchaseItems, fw);
            fw.flush();
            Log.d("PaymentActivity", "Updated purchase.json successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PaymentActivity", "Error writing to purchase.json", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MESSAGE) {
            if (resultCode == RESULT_OK) {
                // Επιστροφή στην BuyingCartActivity
                Intent intent = new Intent(this, BuyingCartActivity.class);
                startActivity(intent);
                finish(); // Κλείσιμο της τρέχουσας δραστηριότητας
            }
        }
    }
}
