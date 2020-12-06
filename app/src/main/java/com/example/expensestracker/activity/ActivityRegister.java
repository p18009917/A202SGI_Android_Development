package com.example.expensestracker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensestracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRegister extends AppCompatActivity {
    private ProgressDialog pd;
    private Toolbar toolbar;
    private EditText etEmail, etPsw;
    private Button btnRegister;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        etEmail = findViewById(R.id.etEmail);
        etPsw = findViewById(R.id.etPsw);
        btnRegister = findViewById(R.id.btnRegister);

        toolbar.setTitle("Register");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPsw.getText().toString())) {
                    etEmail.setError("This field is mandatory");
                    etPsw.setError("This field is mandatory");

                    return;
                }

                pd = new ProgressDialog(ActivityRegister.this);
                pd.setMessage("Processing . . .");
                pd.setCancelable(false);
                pd.show();

                String email = etEmail.getText().toString().trim();
                String password = etPsw.getText().toString();

                register(email, password);
            }
        });
    }

    private void register(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String uid = auth.getCurrentUser().getUid();

                    ref.child(uid).child("balance").setValue(0);
                    ref.child(uid).child("categories").child("income").push().setValue("Allowance");
                    ref.child(uid).child("categories").child("income").push().setValue("Salary");
                    ref.child(uid).child("categories").child("expenses").push().setValue("Food & Beverages");
                    ref.child(uid).child("categories").child("expenses").push().setValue("Transportation");
                    ref.child(uid).child("categories").child("expenses").push().setValue("Entertainment");

                    Toast.makeText(getApplicationContext(), "User successfully registered", Toast.LENGTH_SHORT).show();

                    pd.dismiss();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Register failed", Toast.LENGTH_SHORT).show();

                    pd.dismiss();

                    etEmail.setText("");
                    etPsw.setText("");

                    return;
                }
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