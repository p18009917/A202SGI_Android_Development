package com.example.expensestracker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensestracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog pd;
    private Toolbar toolbar;
    private EditText etEmail, etPsw;
    private Button btnRegister, btnLogin;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        if(auth.getCurrentUser() != null) {
            Toast.makeText(this, "Welcome back.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnLogin: {
                if(TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPsw.getText().toString())) {
                    etEmail.setError("This field is mandatory");
                    etPsw.setError("This field is mandatory");

                    return;
                }

                pd = new ProgressDialog(this);
                pd.setMessage("Logging in . . .");
                pd.setCancelable(false);
                pd.show();

                String email = etEmail.getText().toString().trim();
                String password = etPsw.getText().toString();

                login(email, password);

                break;
            }
            case R.id.btnGotoRegister: {
                Intent intent = new Intent(this, ActivityRegister.class);
                startActivity(intent);

                break;
            }
        }
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();

                    pd.dismiss();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();

                    pd.dismiss();

                    etEmail.setText("");
                    etPsw.setText("");

                    return;
                }
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        etEmail = findViewById(R.id.etEmail);
        etPsw = findViewById(R.id.etPsw);
        btnRegister = findViewById(R.id.btnGotoRegister);
        btnLogin = findViewById(R.id.btnLogin);

        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }
}