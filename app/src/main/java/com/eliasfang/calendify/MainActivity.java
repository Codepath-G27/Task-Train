package com.eliasfang.calendify;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.eliasfang.calendify.API.NotificationApi;
import com.eliasfang.calendify.activities.FirebaseActivity;
import com.eliasfang.calendify.activities.PreferencesActivity;
import com.eliasfang.calendify.dialogs.TaskCreateBottomSheetDialog;
import com.eliasfang.calendify.fragments.CalendarFragment;
import com.eliasfang.calendify.fragments.SocialFragment;
import com.eliasfang.calendify.fragments.ToDoFragment;
import com.eliasfang.calendify.models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main Activity";

    Button btn_login;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private DrawerLayout drawer;
    TextView tvNavName;
    LinearLayout layoutPreferences;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private NotificationApi notificationApi;

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
        tvNavName = (TextView) headerView.findViewById(R.id.tv_Name);
        layoutPreferences = headerView.findViewById(R.id.layout_Preferences);
        layoutPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null )
                    startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
                else
                    Toast.makeText(MainActivity.this, "Please login to edit preferences", Toast.LENGTH_SHORT).show();
            }
        });

        btn_login = findViewById(R.id.btn_login);


        if (auth.getCurrentUser() != null)
            logInUser();
         else
            btn_login.setText("Login");


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FirebaseActivity.class));
            }
        });


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
                    case R.id.nav_social:
                        if(FirebaseAuth.getInstance().getCurrentUser() != null)
                            getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new SocialFragment()).commit();
                        else {
                            Toast.makeText(MainActivity.this, "Please login to view friends", Toast.LENGTH_SHORT).show();
                            getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new ToDoFragment()).commit();
                            navigationView.setCheckedItem(R.id.nav_todo);
                        }
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
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new ToDoFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_todo);
        }
    }

    private void logInUser() {
        btn_login.setText("Log Out");
        tvNavName.setText(auth.getCurrentUser().getDisplayName());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen((GravityCompat.START))) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth.getCurrentUser() != null) {
            btn_login.setText("Log Out");
            tvNavName.setText(auth.getCurrentUser().getDisplayName());
        } else {
            btn_login.setText("Login");
            tvNavName.setText("Anonymous User");
        }

    }

}
