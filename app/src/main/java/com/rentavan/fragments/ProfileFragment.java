package com.rentavan.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentavan.R;
import com.rentavan.activities.StartupActivity;
import com.rentavan.utils.Utils;

public class ProfileFragment extends Fragment {

    FirebaseDatabase RENTAVAN;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    View view;
    TextView tvEmail;
    TextInputEditText etFirstName, etLastName, etMobile;
    MaterialButton btnSave, btnLogout;

    boolean firstNameChanged, lastNameChanged, mobileChanged;
    String firstName, lastName, mobile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeFirebase();
        initialize();
        loadUserProfile();

        btnLogout.setOnClickListener(view1 -> {
            AUTH.signOut();
            startActivity(new Intent(requireActivity(), StartupActivity.class));
            requireActivity().finish();
        });

        btnSave.setOnClickListener(view1 -> {
            btnSave.setEnabled(false);

            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || mobile.isEmpty()) {
                Utils.basicDialog(requireContext(), "Failed to update profile", "All fields are required and can not be left empty");
                return;
            }

            DatabaseReference dbUser = RENTAVAN.getReference("user_"+USER.getUid());
            dbUser.child("firstName").setValue(firstName);
            dbUser.child("lastName").setValue(lastName);
            dbUser.child("mobile").setValue(mobile);

            this.firstName = firstName;
            this.lastName = lastName;
            this.mobile = mobile;
        });

        return view;
    }

    private void loadUserProfile() {
        // email
        if (USER != null){
            tvEmail.setText(USER.getEmail());
        }

        // name
        DatabaseReference dbUser = RENTAVAN.getReference("user_"+USER.getUid());
        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firstName = snapshot.child("firstName").getValue().toString();
                lastName = snapshot.child("lastName").getValue().toString();
                mobile = snapshot.child("mobile").getValue().toString();

                etFirstName.setText(firstName);
                etLastName.setText(lastName);
                etMobile.setText(mobile);

                etFirstName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        firstNameChanged = !charSequence.toString().equals(firstName);

                        handleSaveButton(firstNameChanged, lastNameChanged, mobileChanged);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });

                etLastName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        lastNameChanged = !charSequence.toString().equals(lastName);

                        handleSaveButton(firstNameChanged, lastNameChanged, mobileChanged);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });

                etMobile.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        mobileChanged = !charSequence.toString().equals(mobile);

                        handleSaveButton(firstNameChanged, lastNameChanged, mobileChanged);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize() {
        tvEmail = view.findViewById(R.id.tvEmail);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etMobile = view.findViewById(R.id.etMobile);
        btnSave = view.findViewById(R.id.btnSave);
        btnLogout = view.findViewById(R.id.btnLogout);

        firstNameChanged = false;
        lastNameChanged = false;
        mobileChanged = false;
    }

    private void handleSaveButton(boolean firstNameChanged, boolean lastNameChanged, boolean mobileChanged) {
        btnSave.setEnabled(firstNameChanged || lastNameChanged || mobileChanged);
    }
}