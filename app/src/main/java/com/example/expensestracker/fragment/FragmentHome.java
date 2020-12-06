package com.example.expensestracker.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensestracker.R;
import com.example.expensestracker.activity.ActivityTransactionHistory;
import com.example.expensestracker.adapter.TransactionAdapter;
import com.example.expensestracker.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Looper.getMainLooper;

public class FragmentHome extends Fragment implements View.OnClickListener {
    private View view;

    private LinearLayout fgHome;
    private TextView tvDate, tvBalance, tvViewAll;
    private CardView cvIncome, cvExpenses;
    private RecyclerView rvTransactions;

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference refTrc = db.getReference().child("user").child(uid).child("transactions");
    private DatabaseReference refBalc = db.getReference().child("user").child(uid).child("balance");
    private DatabaseReference refCat = db.getReference().child("user").child(uid).child("categories");

    private Date date;
    private SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
    private Double balance = 0.0;
    private ArrayList<String> catIncome = new ArrayList<>();
    private ArrayList<String> catExpenses = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        init();

        return view;
    }

    @Override
    public void onClick(View v) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_add_transaction, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final TextView tvTitle = view.findViewById(R.id.tvTitle);
        final Spinner spCategory = view.findViewById(R.id.spCategory);
        final EditText etDescription = view.findViewById(R.id.etDescription);
        final EditText etAmount = view.findViewById(R.id.etAmount);
        etAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});

        switch (v.getId()) {
            case R.id.cvIncome: {
                tvTitle.setText("Add New Income");

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, catIncome);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(adapter);

                builder.setCancelable(false)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(etAmount.getText().toString())) {
                            etAmount.setError("This field is mandatory");

                            return;
                        }

                        String key = refTrc.push().getKey();
                        String category = spCategory.getSelectedItem().toString();
                        String description = etDescription.getText().toString();
                        Double total = Double.parseDouble(etAmount.getText().toString());

                        Transaction trc = new Transaction(key, date, "Income", category, description, total);

                        refBalc.setValue(balance + total);

                        refTrc.child(key).setValue(trc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Income successfully deleted", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        });
                    }
                });

                break;
            }
            case R.id.cvExpenses: {
                tvTitle.setText("Add New Expenses");

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, catExpenses);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(adapter);

                builder.setCancelable(false)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(etAmount.getText().toString())) {
                            etAmount.setError("This field is mandatory");

                            return;
                        }

                        String key = refTrc.push().getKey();
                        String category = spCategory.getSelectedItem().toString();
                        String description = etDescription.getText().toString();
                        Double total = Double.parseDouble(etAmount.getText().toString());

                        Transaction trc = new Transaction(key, date, "Expenses", category, description, total);

                        refBalc.setValue(balance - total);

                        refTrc.child(key).setValue(trc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Expenses successfully deleted", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        });
                    }
                });

                break;
            }
            case R.id.tvViewAll: {
                Intent intent = new Intent(getActivity(), ActivityTransactionHistory.class);
                startActivity(intent);

                break;
            }
        }
    }

    private void init() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Wallet Balance");
        fgHome = view.findViewById(R.id.fragment_home);
        tvDate = view.findViewById(R.id.tvDate);
        tvBalance = view.findViewById(R.id.tvBalance);
        cvIncome = view.findViewById(R.id.cvIncome);
        cvExpenses = view.findViewById(R.id.cvExpenses);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        tvViewAll = view.findViewById(R.id.tvViewAll);

        cvIncome.setOnClickListener(this);
        cvExpenses.setOnClickListener(this);
        tvViewAll.setOnClickListener(this);

        //Get current time
        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                date = Calendar.getInstance().getTime();
                String formattedDate = df.format(date);
                tvDate.setText(formattedDate);
                someHandler.postDelayed(this, 1000);
            }
        }, 10);

        //Get wallet balance
        refBalc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    tvBalance.setText("RM 0.00");

                    return;
                }

                balance = dataSnapshot.getValue(Double.class);

                tvBalance.setText(String.format("RM %.2f", balance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get recent transactions
        refTrc.limitToLast(5).addValueEventListener(new ValueEventListener() {
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

                TransactionAdapter rvAdapter = new TransactionAdapter(getActivity(), listTrc);
                rvTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvTransactions.setAdapter(rvAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get Income category
        refCat.child("income").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list = new ArrayList<>();

                for(DataSnapshot snaphot : dataSnapshot.getChildren()) {
                    String category = snaphot.getValue(String.class);
                    list.add(category);
                }

                catIncome = list;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Get expenses category
        refCat.child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list = new ArrayList<>();

                for(DataSnapshot snaphot : dataSnapshot.getChildren()) {
                    String category = snaphot.getValue(String.class);
                    list.add(category);
                }

                catExpenses = list;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    class DecimalDigitsInputFilter implements InputFilter {
        private Pattern mPattern;
        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches()) {
                return "";
            }
            return null;
        }
    }

}