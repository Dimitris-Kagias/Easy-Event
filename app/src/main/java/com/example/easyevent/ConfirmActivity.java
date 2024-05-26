package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmActivity extends AppCompatActivity {

    private CartItem cartItem;
    private boolean isCancellation;
    private boolean isOfferConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Intent intent = getIntent();
        isCancellation = intent.hasExtra("CANCEL_ITEM");
        isOfferConfirmation = intent.hasExtra("offer");

        if (isCancellation) {
            cartItem = (CartItem) intent.getSerializableExtra("CANCEL_ITEM");
        } else if (!isOfferConfirmation) {
            cartItem = (CartItem) intent.getSerializableExtra("CART_ITEM");
        }

        TextView textView = findViewById(R.id.text_view_confirm);
        if (isCancellation) {
            textView.setText("Είστε σίγουρος για την ακύρωση της εκδήλωσης για " + cartItem.getBusinessName() + "?");
        } else if (isOfferConfirmation) {
            textView.setText("Ολοκλήρωση δημιουργίας προσφοράς;");
        } else {
            textView.setText("Ολοκλήρωση συναλλαγής για " + cartItem.getBusinessName() + "?");
        }

        Button yesButton = findViewById(R.id.button_yes);
        Button noButton = findViewById(R.id.button_no);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseYes();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNo();
            }
        });
    }

    private void chooseYes() {
        Intent intent;
        if (isCancellation) {
            intent = new Intent(this, CancelReasonActivity.class);
            intent.putExtra("CANCEL_ITEM", cartItem);
        } else if (isOfferConfirmation) {
            intent = new Intent();
            intent.putExtra("CONFIRMED", true);
            setResult(RESULT_OK, intent);
            finish();
            return;
        } else {
            intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("CART_ITEM", cartItem);
        }
        startActivity(intent);
        finish();
    }

    private void chooseNo() {
        if (isCancellation) {
            Intent intent = new Intent(this, CancelActivity.class);
            startActivity(intent);
        } else if (isOfferConfirmation) {
            Intent intent = new Intent();
            intent.putExtra("CONFIRMED", false);
            setResult(RESULT_OK, intent);
        } else {
            Intent intent = new Intent(this, BuyingCartActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
