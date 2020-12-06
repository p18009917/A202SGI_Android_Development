package com.example.expensestracker.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensestracker.R;
import com.example.expensestracker.adapter.CategoryExpensesAdapter;
import com.example.expensestracker.adapter.CategoryIncomeAdapter;
import com.example.expensestracker.model.Category;
import com.example.expensestracker.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentCategories extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tvAddCatIncome, tvAddCatExpenses;
    private RecyclerView rvCatIncome, rvCatExpenses;

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference refCat = db.getReference().child("user").child(uid).child("categories");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categories, container, false);

        tvAddCatIncome = view.findViewById(R.id.tvAddCatIncome);
        tvAddCatExpenses = view.findViewById(R.id.tvAddCatExpenses);
        rvCatIncome = view.findViewById(R.id.rvCatIncome);
        rvCatExpenses = view.findViewById(R.id.rvCatExpenses);

        tvAddCatIncome.setOnClickListener(this);
        tvAddCatExpenses.setOnClickListener(this);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Categories");

        //Get Income category
        refCat.child("income").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    return;
                }

                ArrayList<Category> list = new ArrayList<>();

                for(DataSnapshot snaphot : dataSnapshot.getChildren()) {
                    String id = snaphot.getKey();
                    String name = snaphot.getValue(String.class);

                    Category category = new Category(id, name);
                    list.add(category);
                }

                CategoryIncomeAdapter rvAdapter = new CategoryIncomeAdapter(getActivity(), list);
                rvCatIncome.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvCatIncome.setAdapter(rvAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Get expenses category
        refCat.child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    return;
                }

                ArrayList<Category> list = new ArrayList<>();

                for(DataSnapshot snaphot : dataSnapshot.getChildren()) {
                    String id = snaphot.getKey();
                    String name = snaphot.getValue(String.class);

                    Category category = new Category(id, name);
                    list.add(category);
                }

                CategoryExpensesAdapter rvAdapter = new CategoryExpensesAdapter(getActivity(), list);
                rvCatExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvCatExpenses.setAdapter(rvAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_add_category, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final TextView tvTitle = view.findViewById(R.id.tvTitle);
        final EditText etName = view.findViewById(R.id.etName);

        switch (v.getId()) {
            case R.id.tvAddCatIncome: {
                tvTitle.setText("Add Income Category");

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
                        if(TextUtils.isEmpty(etName.getText().toString())) {
                            etName.setError("This field is mandatory");

                            return;
                        }

                        String catName = etName.getText().toString();

                        refCat.child("income").push().setValue(catName).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Category successfully added", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        });;
                    }
                });

                break;
            }
            case R.id.tvAddCatExpenses: {
                tvTitle.setText("Add Expense Category");

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
                        if(TextUtils.isEmpty(etName.getText().toString())) {
                            etName.setError("This field is mandatory");

                            return;
                        }

                        String catName = etName.getText().toString();

                        refCat.child("expenses").push().setValue(catName).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Category successfully added", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        });
                    }
                });

                break;
            }
        }
    }
}