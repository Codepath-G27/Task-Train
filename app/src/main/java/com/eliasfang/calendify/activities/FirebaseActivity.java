package com.eliasfang.calendify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eliasfang.calendify.MainActivity;
import com.eliasfang.calendify.R;
import com.eliasfang.calendify.models.FriendRequest;
import com.eliasfang.calendify.models.User;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "FirebaseUIAuthActivity";

    FirebaseAuth auth;
    AuthUI authUI;

    FirebaseFirestore dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_firebase)
                .setEmailButtonId(R.id.btn_email)
                .build();

        auth = FirebaseAuth.getInstance();
        authUI = AuthUI.getInstance();
        dataBase = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null)
            logOutUser();
        else
            logInUser(customLayout);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            //There is a new user
            if (response == null) {
                //The user has pressed the back button
                finish();
            } else if (response.isNewUser()) {
                try {
                    addDocumentData();
                    startActivity(new Intent(FirebaseActivity.this, MainActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_OK) {
                //The user has logged in
                checkToken();
                startActivity(new Intent(FirebaseActivity.this, MainActivity.class));
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
                dataBase.collection("users")
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
        dataBase.collection("friend_requests").document(auth.getUid()).set(request)
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
                dataBase.collection("users")
                        .document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if(!task.getResult().equals(user.getFmcToken())){
                            dataBase.collection("users")
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

    private void logOutUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(FirebaseActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    private void logInUser(AuthMethodPickerLayout customLayout) {
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
        providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());

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

}