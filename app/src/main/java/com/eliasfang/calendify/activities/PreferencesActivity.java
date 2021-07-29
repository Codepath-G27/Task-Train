package com.eliasfang.calendify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eliasfang.calendify.MainActivity;
import com.eliasfang.calendify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class PreferencesActivity extends AppCompatActivity {

    private final String TAG = "PreferenceActivity";

    private EditText enterName;
    private TextView enterEmail;
    private TextView enterPassword;
    private ImageButton btnClose;
    private TextView btnSave;

    private FirebaseAuth auth;
    private FirebaseFirestore dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        enterName = findViewById(R.id.tv_EnterName);
        enterEmail = findViewById(R.id.tv_enterEmail);
        enterPassword = findViewById(R.id.tvEnterPassword);

        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseFirestore.getInstance();

        enterName.setHint(auth.getCurrentUser().getDisplayName());
        enterEmail.setText(auth.getCurrentUser().getEmail());


        btnClose = findViewById(R.id.imgBtnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        enterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    Toast.makeText(PreferencesActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnSave = findViewById(R.id.tvSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();

                if(!enterName.getText().toString().isEmpty())
                    updateUserProfile(user);
                else
                    finish();
            }
        });

    }

    private void updateUserProfile(FirebaseUser user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(enterName.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "User profile updated.");
                            DocumentReference document =  dataBase.collection("users").document(auth.getUid());
                            document.update("displayName", enterName.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                            Toast.makeText(PreferencesActivity.this, "Error Updating Name", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }
}