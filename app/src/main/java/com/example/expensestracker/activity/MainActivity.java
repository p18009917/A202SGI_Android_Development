package com.example.expensestracker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.expensestracker.fragment.FragmentCategories;
import com.example.expensestracker.fragment.FragmentHome;
import com.example.expensestracker.R;
import com.example.expensestracker.fragment.FragmentStatistics;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView nav_view;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextView userEmail;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        nav_view = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);

        //Init toolbar
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Init drawer header
        View headerView = nav_view.getHeaderView(0);
        userEmail = headerView.findViewById(R.id.userEmail);
        String curUser = auth.getCurrentUser().getEmail();
        userEmail.setText(curUser);

        //Init drawer onclick events
        nav_view.setNavigationItemSelectedListener(this);

        //Init fragment
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frame_layout, new FragmentHome());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction ft = fm.beginTransaction();

        switch (item.getItemId()) {
            case R.id.nav_balance: {
                ft.replace(R.id.frame_layout, new FragmentHome());
                ft.addToBackStack(null);

                break;
            }
            case R.id.nav_categories: {
                ft.replace(R.id.frame_layout, new FragmentCategories());
                ft.addToBackStack(null);

                break;
            }
            case R.id.nav_statistics: {
                ft.replace(R.id.frame_layout, new FragmentStatistics());
                ft.addToBackStack(null);

                break;
            }
            case R.id.nav_logout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Message");
                builder.setMessage("Logout?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();

                        finishAffinity();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            }
        }

        ft.commit();
        drawer.closeDrawers();

        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Message");
        builder.setMessage("Exit the app?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}