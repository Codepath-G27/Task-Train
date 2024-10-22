package com.eliasfang.calendify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.dialogs.LoadingDialog;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PreferencesActivity extends AppCompatActivity {

    private final String TAG = "PreferenceActivity";

    private EditText enterName;
    private TextView enterEmail;
    private TextView enterPassword;
    private ImageButton btnClose;
    private TextView btnSave;

    private ImageView iv_profile;
    private TextView tv_imageName;
    private ImageView ib_left;
    private ImageView ib_right;

    private FirebaseAuth auth;
    private FirebaseFirestore dataBase;

    private int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        enterName = findViewById(R.id.et_EnterName);
        enterEmail = findViewById(R.id.tv_enterEmail);
        enterPassword = findViewById(R.id.tvEnterPassword);

        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseFirestore.getInstance();

        enterName.setHint(auth.getCurrentUser().getDisplayName());
        enterEmail.setText(auth.getCurrentUser().getEmail());

        iv_profile = findViewById(R.id.iv_profileIcon);

        tv_imageName = findViewById(R.id.tv_imageName);

        ib_right = findViewById(R.id.iv_right);
        ib_left = findViewById(R.id.iv_left);

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.start();

        dataBase.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    index = user.getIcon();
                    setIcon();
                    loadingDialog.dismiss();
                }
            }
        });


        ib_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index != 4)
                    index++;
                else
                    index = 0;
                setIcon();

            }
        });

        ib_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index != 0)
                    index--;
                else
                    index = 4;
                setIcon();
            }
        });


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
                                    Toast.makeText(PreferencesActivity.this, getResources().getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnSave = findViewById(R.id.btn_accept);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();

                updateUserProfile(user);


            }
        });

    }

    private void setIcon() {
        switch (index) {
            case 0:
                iv_profile.setImageResource(R.mipmap.ic_ghosty_guy_round);
                tv_imageName.setText("Ghosty Guy");
                break;
            case 1:
                iv_profile.setImageResource(R.mipmap.ic_ticked_master_teddy_round);
                tv_imageName.setText("Ticket Master Teddy");
                break;
            case 2:
                iv_profile.setImageResource(R.mipmap.ic_passenger_piggy_round);
                tv_imageName.setText("Passenger Piggy");
                break;
            case 3:
                iv_profile.setImageResource(R.mipmap.ic_conductor_cat_round);
                tv_imageName.setText("Conductor Cat");
                break;
            case 4:
                iv_profile.setImageResource(R.mipmap.ic_railroad_rabbit_round);
                tv_imageName.setText("Railroad Rabbit");
                break;
        }
    }

    private void updateUserProfile(FirebaseUser user) {


            Log.i(TAG, "User profile updating");
            UserProfileChangeRequest profileUpdates;
            if (enterName.getText().toString().isEmpty()) {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .build();
            } else {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(enterName.getText().toString())
                        .build();
            }

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "User profile updated.");
                                DocumentReference document = dataBase.collection("users").document(auth.getUid());
                                document.update("icon", index)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i(TAG, "DocumentSnapshot successfully updated!");
                                                if (!enterName.getText().toString().isEmpty() && enterName.getText().toString().length() <= 15 && enterName.getText().toString().length() > 4) {
                                                    document.update("displayName", enterName.getText().toString())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.i(TAG, "DocumentSnapshot successfully updated!");
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.i(TAG, "Error updating document", e);
                                                                    Toast.makeText(PreferencesActivity.this, getResources().getString(R.string.error_updating_name), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                                else if(!enterName.getText().toString().isEmpty())
                                                    Toast.makeText(PreferencesActivity.this, "Name must be between 5 and 20 characters long", Toast.LENGTH_SHORT).show();
                                                else
                                                    finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG, "Error updating document", e);
                                                Toast.makeText(PreferencesActivity.this, getResources().getString(R.string.error_updating_name), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        }
                    });

    }


}