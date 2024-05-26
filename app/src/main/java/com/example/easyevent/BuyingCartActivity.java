package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BuyingCartActivity extends AppCompatActivity {

    private ListView listViewCart;
    private List<CartItem> cartItemList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buying_cart);

        listViewCart = findViewById(R.id.listView_cart);
        cartItemList = new ArrayList<>();

        loadCartItems();

        if (cartItemList == null || cartItemList.isEmpty()) {
            showEmptyCartMessage();
        } else {
            listViewCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CartItem selectedCartItem = cartItemList.get(position);
                    choose_BusinCart(selectedCartItem);
                }
            });
        }
    }

    private void loadCartItems() {
        File file = new File(getFilesDir(), "easyevent.json");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CartItem>>() {}.getType();
                cartItemList = gson.fromJson(jsonBuilder.toString(), listType);

                if (cartItemList == null) {
                    cartItemList = new ArrayList<>();
                }

                List<String> cartItemNames = new ArrayList<>();
                for (CartItem item : cartItemList) {
                    if (item != null) {
                        cartItemNames.add(item.getBusinessName());
                    }
                }

                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cartItemNames);
                listViewCart.setAdapter(adapter);

            } catch (FileNotFoundException e) {
                Log.e("BuyingCartActivity", "File not found: " + e.getMessage());
                Toast.makeText(this, "Το αρχείο δεν βρέθηκε", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("BuyingCartActivity", "Error reading file: " + e.getMessage(), e);
                Toast.makeText(this, "Σφάλμα κατά την ανάγνωση του αρχείου", Toast.LENGTH_SHORT).show();
            }
        } else {
            cartItemList = new ArrayList<>();  // Ensure the list is initialized if the file doesn't exist
            Toast.makeText(this, "Δεν υπάρχουν διαθέσιμες επιχειρήσεις στο καλάθι", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEmptyCartMessage() {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", "Το καλάθι είναι άδειο. Παρακαλώ προσθέστε επιχειρήσεις.");
        startActivityForResult(intent, 1);
    }

    private void choose_BusinCart(CartItem cartItem) {
        Intent intent = new Intent(this, ConfirmActivity.class);
        intent.putExtra("CART_ITEM", cartItem);
        startActivity(intent);
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
