package com.eliasfang.calendify.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.API.NotificationApi;
import com.eliasfang.calendify.API.RetrofitInit;
import com.eliasfang.calendify.Adapter.CreateFriendsAdapter;
import com.eliasfang.calendify.R;
import com.eliasfang.calendify.models.PushNotification;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskCreateBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "CreateBottomSheetDialog";

    private BottomSheetListener listener;

    private RecyclerView rvFriends;
    private ImageButton iv_select;
    private FirebaseFirestore database;
    private FirebaseAuth auth;

    //Use a loading dialog to hide accessing the friends list
    //private LoadingDialog loadingDialog;

    private CreateFriendsAdapter adapter;
    private List<User> users = new ArrayList<User>();

    private NotificationApi notificationApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_create_bottom_sheet_layout, container, false);

        //Retrofit
        Retrofit retro = RetrofitInit.getClient();
        notificationApi = retro.create(NotificationApi.class);



        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        rvFriends = view.findViewById(R.id.rv_friends);
        iv_select = view.findViewById(R.id.iv_send);

        adapter = new CreateFriendsAdapter(getContext(), users);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        getFriends();

        iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClicked(adapter.getSelectedList());
                dismiss();
            }


        });
        return view;
    }

    private void getFriends() {
        DocumentReference docRef = database.collection("users").document(auth.getUid());

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    users.clear();
                    User user = snapshot.toObject(User.class);
                    for (String id : user.getFriends()) {
                        Log.i(TAG, user.getFriends().toString());
                        DocumentReference ref = database.collection("users").document(id);
                        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                users.add(user);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    //loadingDialog.dismiss();

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }


    private void createNotification(PushNotification notification) {

        Call<PushNotification> call = notificationApi.postNotification(notification);

        call.enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error: " + response.code());
                    return;
                } else {
                    Log.i(TAG, "Error: " + response.code());
                    Gson gson = new Gson();
                    //Log.d(TAG, "Response: " + gson.toJson(response));
                    Log.i(TAG, "Body:" + response.body());
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {

            }
        });
    }

    public interface BottomSheetListener {
        void onButtonClicked(List<User> selectedUsers);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (BottomSheetListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }

    }
}
