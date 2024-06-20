package com.rentavan.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentavan.R;
import com.rentavan.adapters.InboxAdapter;
import com.rentavan.objects.Inbox;
import com.rentavan.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class InboxFragment extends Fragment implements InboxAdapter.OnInboxListener {

    FirebaseDatabase RENTAVAN;
    FirebaseUser USER;
    ValueEventListener velInbox;

    private void initializeFirebase() {
        USER = FirebaseAuth.getInstance().getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    ArrayList<Inbox> arrInbox;
    InboxAdapter inboxAdapter;
    InboxAdapter.OnInboxListener onBookingListener = this;

    RecyclerView rvInbox;
    TextView tvEmpty;
    CircularProgressIndicator loadingBar;

    int userType = 0;

    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeFirebase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inbox, container, false);

        initialize();
        loadInbox();

        return view;
    }

    private void loadInbox() {
        if (userType == 1) { // admin
            loadAdminInbox();
        }
        else { // driver (?)
        }
    }

    private void loadAdminInbox() {
        arrInbox = new ArrayList<>();
        rvInbox = view.findViewById(R.id.rvInbox);
        loadingBar = view.findViewById(R.id.loadingBar);
        rvInbox.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference dbInbox = RENTAVAN.getReference("chat");
        velInbox = dbInbox.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrInbox.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvInbox.setVisibility(View.INVISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvInbox.setVisibility(View.VISIBLE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Inbox chatReference = dataSnapshot.getValue(Inbox.class);
                    arrInbox.add(chatReference);
                }

                loadingBar.hide();

                inboxAdapter = new InboxAdapter(getContext(), arrInbox, onBookingListener);
                rvInbox.setAdapter(inboxAdapter);
                inboxAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize() {
        rvInbox = view.findViewById(R.id.rvInbox);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        loadingBar = view.findViewById(R.id.loadingBar);

        userType = Utils.Cache.getInt(requireContext(), "user_type");
    }

    @Override
    public void onInboxClick(int position) {
        String passengerUid = arrInbox.get(position).getPassengerUid();

        DatabaseReference dbCustomerName = RENTAVAN.getReference("user_"+passengerUid);
        dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue().toString();
                String lastName = snapshot.child("lastName").getValue().toString();

                Bundle chatArgs = new Bundle();
                chatArgs.putString("passenger_uid", passengerUid);
                chatArgs.putString("passenger_name", firstName+" "+lastName);

                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(chatArgs);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHolder, chatFragment, "CHAT_FRAGMENT")
                        .addToBackStack("CHAT_FRAGMENT")
                        .commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}