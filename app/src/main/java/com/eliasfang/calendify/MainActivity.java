package com.eliasfang.calendify;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.eliasfang.calendify.fragments.CalendarFragment;
import com.eliasfang.calendify.fragments.ToDoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private DrawerLayout drawer;
    TextView tvNavEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //To Set the Header Name Text View
        View headerView = navigationView.getHeaderView(0);
        tvNavEmail= (TextView) headerView.findViewById(R.id.tv_Name);
        tvNavEmail.setText("Enter Name Here");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_todo:
                        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new ToDoFragment()).commit();
                        break;
                    case R.id.nav_calendar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new CalendarFragment()).commit();
                        break;

                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //To Start the to-do Fragment once the app opens and saves the same fragment if rotated <- That's why if statement
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new ToDoFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_todo);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen((GravityCompat.START))) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}
