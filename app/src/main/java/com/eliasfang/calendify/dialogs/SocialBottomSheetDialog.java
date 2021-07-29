package com.eliasfang.calendify.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eliasfang.calendify.API.NotificationApi;
import com.eliasfang.calendify.API.RetrofitInit;
import com.eliasfang.calendify.R;
import com.eliasfang.calendify.models.FriendRequest;
import com.eliasfang.calendify.models.NotificationData;
import com.eliasfang.calendify.models.PushNotification;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SocialBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "SocialBottomSheetDialog";
    private ImageButton btnSend;
    private EditText etUserEmail;
    private FirebaseFirestore database;
    private FirebaseAuth auth;

    private NotificationApi notificationApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_bottom_sheet_layout, container, false);

        //Retrofit
        Retrofit retro = RetrofitInit.getClient();
        notificationApi = retro.create(NotificationApi.class);


        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        btnSend = view.findViewById(R.id.iv_send);
        etUserEmail = view.findViewById(R.id.tv_EnterName);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etUserEmail.getText().toString();


                CollectionReference userReference = database.collection("users");

                userReference
                        .whereEqualTo("email", name)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        Toast.makeText(getContext(), "Please Enter a Valid User Email", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    } else {

                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            User user = document.toObject(User.class);
                                            if (user.getEmail().equals(auth.getCurrentUser().getEmail()) || user.getFriends().contains(auth.getCurrentUser().getUid()))
                                                Toast.makeText(getContext(), "Please Enter a Valid User Email", Toast.LENGTH_SHORT).show();
                                            else {
                                                //CHECK IF THE REQUEST WAS ALREADY SENT
                                                //CHECK THAT IT IS NOT THE SAME USERNAME AS YOURS
                                                //Email is valid and can be requested
                                                Log.i(TAG, "Email is valid: Sending Request");

                                                //Send notification to requested user
                                                NotificationData data = new NotificationData("New Friend Request", "The user " + auth.getCurrentUser().getEmail() + " has sent you a friend request!");
                                                PushNotification notification = new PushNotification(data, user.getFmcToken());
                                                createNotification(notification);

                                                String toId = document.getId();
                                                String fromId = auth.getUid();
                                                sendFriendRequest(toId, fromId);
                                                receiveFriendRequest(fromId, toId);

                                            }
                                            dismiss();
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }


        });
        return view;
    }

    private void sendFriendRequest(String toId, String fromId) {


        DocumentReference documentReference = database.collection("friend_requests").document(fromId);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        FriendRequest requests = document.toObject(FriendRequest.class);
                        Map<String, ArrayList<String>> map = requests.getRequests();
                        ArrayList<String> data = new ArrayList();

                        data.add(fromId);
                        data.add(toId);
                        map.put(toId, data);

                        documentReference.update("requests", map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });


                        Log.e(TAG, String.valueOf(requests));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void receiveFriendRequest(String toId, String fromId) {


        DocumentReference documentReference = database.collection("friend_requests").document(fromId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        FriendRequest requests = document.toObject(FriendRequest.class);
                        Log.e(TAG, String.valueOf(requests));


                        ArrayList<String> data = new ArrayList();
                        data.add(toId);
                        data.add(fromId);
                        Map<String, ArrayList<String>> map = requests.getRequests();
                        map.put(toId, data);

                        documentReference.update("requests", map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });


                        Log.e(TAG, String.valueOf(requests));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void createNotification(PushNotification notification) {

        Call<PushNotification> call = notificationApi.postNotification(notification);

        call.enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG,"Error: " + response.code());
                    return;
                }
                else{
                    Log.i(TAG,"Error: " + response.code());
                    Gson gson= new Gson();
                    //Log.d(TAG, "Response: " + gson.toJson(response));
                    Log.i(TAG,"Body:" + response.body());
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {

            }
        });
    }

}
