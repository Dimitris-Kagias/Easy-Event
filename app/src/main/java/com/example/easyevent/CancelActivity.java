package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CancelActivity extends AppCompatActivity {

    private ListView listViewPurchases;
    private List<CartItem> purchaseItemList;
    private ArrayAdapter<String> adapter;
    private static final String TAG = "CancelActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);

        listViewPurchases = findViewById(R.id.listView_purchases);
        purchaseItemList = new ArrayList<>();

        loadPurchaseItems();

        if (purchaseItemList.isEmpty()) {
            showEmptyPurchaseMessage();
        } else {
            setupListView();
        }
    }

    private void loadPurchaseItems() {
        File file = new File(getFilesDir(), "purchase.json");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CartItem>>() {}.getType();
                purchaseItemList = gson.fromJson(jsonBuilder.toString(), listType);

                if (purchaseItemList == null) {
                    purchaseItemList = new ArrayList<>();
                }

                List<CartItem> filteredList = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date currentDate = new Date();

                for (CartItem item : purchaseItemList) {
                    if (item != null) {
                        try {
                            Date eventDate = dateFormat.parse(item.getDate());
                            if (eventDate != null && eventDate.after(currentDate)) {
                                filteredList.add(item);
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "Error parsing date: " + e.getMessage(), e);
                        }
                    }
                }

                purchaseItemList = filteredList;

            } catch (IOException e) {
                Log.e(TAG, "Error reading file: " + e.getMessage(), e);
                Toast.makeText(this, "Σφάλμα κατά την ανάγνωση του αρχείου", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Δεν υπάρχουν προγραμματισμένες εκδηλώσεις για ακύρωση", Toast.LENGTH_SHORT).show();
            showEmptyPurchaseMessage();
        }
    }

    private void setupListView() {
        List<String> purchaseItemNames = new ArrayList<>();
        for (CartItem item : purchaseItemList) {
            purchaseItemNames.add(item.getBusinessName() + " - " + item.getDate() + " " + item.getTime());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, purchaseItemNames);
        listViewPurchases.setAdapter(adapter);

        listViewPurchases.setOnItemClickListener((parent, view, position, id) -> {
            CartItem selectedPurchaseItem = purchaseItemList.get(position);
            chooseCancel(selectedPurchaseItem);
        });
    }

    private void showEmptyPurchaseMessage() {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", "Δεν έχετε κάποια προγραμματισμένη εκδήλωση. Παρακαλώ δημιουργήστε μία.");
        startActivityForResult(intent, 1);
    }

    private void chooseCancel(CartItem cartItem) {
        Intent intent = new Intent(this, ConfirmActivity.class);
        intent.putExtra("CANCEL_ITEM", cartItem);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) {
            Intent intent = new Intent(this, CustomerHomePageActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
