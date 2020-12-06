package com.example.expensestracker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.expensestracker.R;

public class ActivityTransactionDetails extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvAmount, tvType, tvCategory, tvDescription, tvDateTime, tvTrcId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        toolbar = findViewById(R.id.toolbar);
        tvAmount = findViewById(R.id.tvAmount);
        tvType = findViewById(R.id.tvType);
        tvCategory = findViewById(R.id.tvCategory);
        tvDescription = findViewById(R.id.tvDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvTrcId = findViewById(R.id.tvTrcId);

        toolbar.setTitle("Transaction Details");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        tvTrcId.setText(intent.getStringExtra("id"));
        tvDateTime.setText(intent.getStringExtra("date"));
        tvType.setText(intent.getStringExtra("type"));
        tvCategory.setText(intent.getStringExtra("category"));
        tvDescription.setText(intent.getStringExtra("description"));

        Double amt = intent.getDoubleExtra("amount", 0.0);
        if(intent.getStringExtra("type").equals("Income")) {
            tvAmount.setText(String.format("+ RM %.2f", amt));

            tvAmount.setTextColor(this.getResources().getColor(R.color.green_800));
        }
        else if(intent.getStringExtra("type").equals("Expenses")) {
            tvAmount.setText(String.format("- RM %.2f", amt));

            tvAmount.setTextColor(this.getResources().getColor(R.color.red_800));
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}