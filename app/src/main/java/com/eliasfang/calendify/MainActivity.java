package com.eliasfang.calendify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.eliasfang.calendify.activities.AlarmBuddyActivity;
import com.eliasfang.calendify.activities.FirebaseActivity;
import com.eliasfang.calendify.activities.PreferencesActivity;
import com.eliasfang.calendify.dialogs.TaskCreateBottomSheetDialog;
import com.eliasfang.calendify.fragments.CalendarFragment;
import com.eliasfang.calendify.fragments.SocialFragment;
import com.eliasfang.calendify.fragments.ToDoFragment;
import com.eliasfang.calendify.models.FriendRequest;
import com.eliasfang.calendify.models.User;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    public static final String TAG = "Main Activity";

    Button btn_login;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private DrawerLayout drawer;
    TextView tvNavName;
    LinearLayout layoutPreferences;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    private NotificationApi notificationApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_login = findViewById(R.id.btn_login);
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_main)
                .setEmailButtonId(R.id.btn_login)
                .build();

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




        if (auth.getCurrentUser() != null) {
            btn_login.setText("Log Out");
            tvNavName.setText(auth.getCurrentUser().getDisplayName());
        }
        else
            btn_login.setText("Login");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null)
                        logOutUser();
                else
                    logInUser(customLayout);

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

    private void logOutUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    private void logInUser(AuthMethodPickerLayout customLayout) {
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());

        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.MainTheme)
                        .setAuthMethodPickerLayout(customLayout)
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            //There is a new user
            if (response == null) {
                //The user has pressed the back button
            } else if (response.isNewUser()) {
                try {
                    addDocumentData();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_OK) {
                //The user has logged in
                checkToken();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            } else {
                //the sign in failed due to other reasons
            }
        }
    }

    public void addDocumentData() throws Exception {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        String displayName = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String token = task.getResult();
                User data = new User(email, displayName, token, auth.getUid());
                database.collection("users")
                        .document(uid)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

            }
        });

        Map<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
        FriendRequest request = new FriendRequest(data);
        database.collection("friend_requests").document(auth.getUid()).set(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void checkToken(){
        Log.w(TAG, "Checking if FCM token is still valid");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                database.collection("users")
                        .document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if(!task.getResult().equals(user.getFmcToken())){
                            database.collection("users")
                                    .document(auth.getUid()).update("fmcToken", task.getResult()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "User FCM token updated", task.getException());
                                }
                            });
                        }
                    }
                });
            }
        });
    }

}
