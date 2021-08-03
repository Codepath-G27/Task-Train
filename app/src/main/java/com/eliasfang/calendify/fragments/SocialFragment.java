package com.eliasfang.calendify.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Adapter.RequestsAdapter;
import com.eliasfang.calendify.Adapter.SocialFriendsAdapter;
import com.eliasfang.calendify.R;
import com.eliasfang.calendify.dialogs.SocialBottomSheetDialog;
import com.eliasfang.calendify.dialogs.LoadingDialog;
import com.eliasfang.calendify.models.FriendRequest;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SocialFragment extends Fragment {

    private static final String TAG = "SocialFragment";
    private TextView tvNumRequests;
    private FirebaseFirestore database;
    private FirebaseAuth auth;

    private RequestsAdapter adapter;

    private SocialFriendsAdapter friendsAdapter;

    private LoadingDialog loadingDialog;

    private NestedScrollView socialRequestsView;

    private List<User> users = new ArrayList<User>();
    private List<User> friends = new ArrayList<User>();

    private FriendRequest requestList = new FriendRequest(new HashMap<String, ArrayList<String>>());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);

        RecyclerView rvRequests = view.findViewById(R.id.rvRequests);

        RecyclerView rvFriends = view.findViewById(R.id.rv_Friends);

        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);

        setHasOptionsMenu(true);

        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        database= FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        tvNumRequests = view.findViewById(R.id.tvNumRequests);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.start();



        socialRequestsView = view.findViewById(R.id.socialRequestsView);
        socialRequestsView.setVisibility(View.INVISIBLE);

        adapter = new RequestsAdapter(getContext(), requestList, users);
        rvRequests.setAdapter(adapter);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        friendsAdapter = new SocialFriendsAdapter(getContext(),friends);
        rvFriends.setAdapter(friendsAdapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));


        getRequests();
        getUsers();
        getFriends();


    }

    private void getUsers() {
       CollectionReference reference = database.collection("users");

       reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               if (error != null) {
                   Log.w(TAG, "Listen failed.", error);
                   return;
               }

               List<User> userList = value.toObjects(User.class);
               users.clear();
               users.addAll(userList);
               adapter.notifyDataSetChanged();
           }
       });
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

                    friends.clear();
                    User user = snapshot.toObject(User.class);
                    for (String id : user.getFriends()) {
                        Log.i(TAG, user.getFriends().toString());
                        DocumentReference ref = database.collection("users").document(id);
                        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                friends.add(user);
                                friendsAdapter.notifyDataSetChanged();
                                Log.i(TAG, "Logged in user friends data has changed");
                                Log.i(TAG, "The user data is: " + user.getUid());
                                Log.i(TAG, "The user data friends list is: " + user.getFriends());
                            }
                        });
                    }
                    friendsAdapter.notifyDataSetChanged();

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void getRequests() {
        DocumentReference documentReference = database.collection("friend_requests").document(auth.getUid());

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value == null) {
                    Log.e(TAG, "Error when getting data");
                }


                FriendRequest requests = value.toObject(FriendRequest.class);

                Log.e(TAG, String.valueOf(requests));
                if(requests!=null){
                    tvNumRequests.setText(String.valueOf(requests.getRequests().size()));
                    if(requests.getRequests().size() == 0){
                        socialRequestsView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        socialRequestsView.setVisibility(View.VISIBLE);
                    }
                    //Remove loading dialog after .3 seconds to make loading more appealing to user
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();
                        }
                    }, 300);

                    requestList.getRequests().clear();
                    requestList.getRequests().putAll(requests.getRequests());
                    adapter.notifyDataSetChanged();
                    Set<String> keys = requests.getRequests().keySet();
                    for(String key : keys)
                        Log.e(TAG, requests.getRequests().get(key).get(1));
                }

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_social, menu);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                SocialBottomSheetDialog sheetDialog = new SocialBottomSheetDialog();
                sheetDialog.show(getChildFragmentManager(), "Social Bottom Sheet");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
