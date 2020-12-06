package com.example.expensestracker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.expensestracker.R;
import com.example.expensestracker.adapter.TransactionAdapter;
import com.example.expensestracker.model.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityTransactionHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvAllTrc;

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference ref = db.getReference().child("user").child(uid).child("transactions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        toolbar = findViewById(R.id.toolbar);
        rvAllTrc = findViewById(R.id.rvAllTrc);

        toolbar.setTitle("Transaction History");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    return;
                }

                final ArrayList<Transaction> listTrc = new ArrayList<>();

                for(DataSnapshot snaphot : dataSnapshot.getChildren()) {
                    Transaction trc = snaphot.getValue(Transaction.class);
                    listTrc.add(0, trc);
                }

                TransactionAdapter rvAdapter = new TransactionAdapter(getApplicationContext(), listTrc);
                rvAllTrc.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rvAllTrc.setAdapter(rvAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}