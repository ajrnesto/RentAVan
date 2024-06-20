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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentavan.R;
import com.rentavan.adapters.ChatAdapter;
import com.rentavan.objects.Chat;
import com.rentavan.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class ChatFragment extends Fragment implements ChatAdapter.OnChatListener {

    FirebaseDatabase RENTAVAN;
    FirebaseUser USER;
    DatabaseReference dbChat;
    ValueEventListener velChat;

    private void initializeFirebase() {
        USER = FirebaseAuth.getInstance().getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    View view;
    RecyclerView rvChat;
    TextView tvEmpty;
    MaterialButton btnSend;
    TextInputEditText etChatBox;
    CircularProgressIndicator loadingBar;

    ArrayList<Chat> arrChat;
    ChatAdapter chatAdapter;
    ChatAdapter.OnChatListener onChatListener = this;

    // top action bar elements
    MaterialButton btnBack;
    TextView tvActivityTitle;

    String chat = "";
    int userType = 0;
    Bundle chatArgs;
    String passengerUid = "", passengerName = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        initializeFirebase();
        initialize();
        initializeTopActionBar();
        loadRecyclerView();

        btnSend.setOnClickListener(view -> {
            chat = Objects.requireNonNull(etChatBox.getText()).toString().trim();
            if (chat.isEmpty()) {
                return;
            }
            sendMessage();
            etChatBox.getText().clear();
        });

        return view;
    }

    private void loadRecyclerView() {
        arrChat = new ArrayList<>();
        rvChat = view.findViewById(R.id.rvChat);
        rvChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);

        if (userType == 0) { // passenger
            passengerUid = USER.getUid();
        }
        else if (userType == 1) { // admin
            passengerUid = chatArgs.getString("passenger_uid");
            passengerName = chatArgs.getString("passenger_name");
        }
        else { // driver (?)
        }
        dbChat = RENTAVAN.getReference("chat_"+passengerUid);
        velChat = dbChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrChat.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    arrChat.add(chat);
                    chatAdapter.notifyDataSetChanged();
                    rvChat.scrollToPosition(arrChat.size() - 1);
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatAdapter = new ChatAdapter(getContext(), arrChat, onChatListener);
        rvChat.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }

    private void sendMessage() {
        if (userType == 0) { // passenger
            passengerUid = USER.getUid();
        }
        else if (userType == 1) { // admin
            passengerUid = chatArgs.getString("passenger_uid");
        }
        else { // driver (?)
        }

        DatabaseReference dbChat = RENTAVAN.getReference("chat_"+passengerUid).push();
        Chat newChat = new Chat(dbChat.getKey(), chat, USER.getUid(), System.currentTimeMillis());
        dbChat.setValue(newChat);

        DatabaseReference dbInbox = RENTAVAN.getReference("chat/"+passengerUid);
        dbInbox.setValue(newChat);
        dbInbox.child("passengerUid").setValue(passengerUid);
    }

    private void initialize() {
        rvChat = view.findViewById(R.id.rvChat);
        etChatBox = view.findViewById(R.id.etChatBox);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        btnSend = view.findViewById(R.id.btnSend);
        loadingBar = view.findViewById(R.id.loadingBar);

        userType = Utils.Cache.getInt(requireContext(), "user_type");
        chatArgs = getArguments();
    }

    private void initializeTopActionBar() {
        tvActivityTitle = view.findViewById(R.id.tvActivityTitle);
        btnBack = view.findViewById(R.id.btnActionBar);

        if (userType == 0) { // passenger
            tvActivityTitle.setText("Rent A Van");
        }
        else if (userType == 1) { // admin
            passengerName = chatArgs.getString("passenger_name");
            tvActivityTitle.setText(passengerName);
        }
        else { // driver (?)
        }

        btnBack.setOnClickListener(view1 -> {
            requireActivity().onBackPressed();
        });
    }

    @Override
    public void onChatClick(int position) {

    }
}