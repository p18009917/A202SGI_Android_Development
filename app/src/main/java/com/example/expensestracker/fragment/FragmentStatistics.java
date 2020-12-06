package com.example.expensestracker.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.expensestracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Hashtable;

public class FragmentStatistics extends Fragment {
    private View view;
    private BarChart barChart;
    private PieChart pieChart;

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference refTrc = db.getReference().child("user").child(uid).child("transactions");

    private Double incomeAmt;
    private Double expensesAmt;
    private Hashtable<String, Float> table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistics, container, false);

        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Statistics");

        createBarChart();
        createPieChart();

        return view;
    }

    private void createBarChart() {
        incomeAmt = 0.0;
        expensesAmt = 0.0;

        //Retrieve data from Firebase
        refTrc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    return;
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("type").getValue(String.class).equals("Income"))
                        incomeAmt += snapshot.child("amount").getValue(Double.class);
                    else if(snapshot.child("type").getValue(String.class).equals("Expenses"))
                        expensesAmt += snapshot.child("amount").getValue(Double.class);
                }

                float min = incomeAmt.floatValue() - expensesAmt.floatValue();

                //Chart appearance
                barChart.setTouchEnabled(false);
                barChart.getDescription().setEnabled(false);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setDrawLabels(false);
                YAxis yAxisL = barChart.getAxisLeft();
                yAxisL.setAxisMinimum(min);
                YAxis yAxisR = barChart.getAxisRight();
                yAxisR.setAxisMinimum(min);

                //Initialize data
                ArrayList<BarEntry> income = new ArrayList<>();
                ArrayList<BarEntry> expenses = new ArrayList<>();

                income.add(new BarEntry(0, incomeAmt.floatValue()));
                expenses.add(new BarEntry(1, expensesAmt.floatValue()));

                BarDataSet set1 = new BarDataSet(income, "Income");
                set1.setColor(getActivity().getResources().getColor(R.color.green_800));
                BarDataSet set2 = new BarDataSet(expenses, "Expenses (RM)");
                set2.setColor(getActivity().getResources().getColor(R.color.red_800));

                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                dataSets.add(set2);

                //Draw graph
                BarData data = new BarData(dataSets);
                data.setValueTextSize(12f);
                barChart.setData(data);
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createPieChart() {
        table = new Hashtable<>();

        refTrc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("type").getValue(String.class).equals("Income"))
                        continue;

                    String cat = snapshot.child("category").getValue(String.class);
                    Float amt = snapshot.child("amount").getValue(Double.class).floatValue();

                    if(table.get(cat) == null)
                        table.put(cat, amt);
                    else
                        table.put(cat, table.get(cat) + amt);
                }

                pieChart.setTouchEnabled(false);
                pieChart.getDescription().setEnabled(false);
                pieChart.setDrawEntryLabels(false);

                ArrayList<PieEntry> entry = new ArrayList<>();
                for (String key : table.keySet()) {
                    entry.add(new PieEntry(table.get(key), key));
                }

                PieDataSet dataSet = new PieDataSet(entry, "(RM)");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                PieData data = new PieData(dataSet);
                data.setValueTextSize(15f);
                pieChart.setData(data);
                pieChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}