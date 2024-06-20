package com.rentavan.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.rentavan.utils.Utils;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

    FirebaseDatabase RENTAVAN;
    FirebaseAuth AUTH;
    FirebaseUser USER;
    DatabaseReference dbUser;

    private void initializeFirebase() {
        AUTH = FirebaseAuth.getInstance();
        USER = FirebaseAuth.getInstance().getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    ConstraintLayout clLogo, clLogin, clSignup;
    // log in
    TextInputEditText etLoginEmail, etLoginPassword;
    MaterialButton btnLogin, btnGotoSignup, btnSkipAuth;
    // sign up
    TextInputEditText etSignupFirstName, etSignupLastName, etSignupMobile, etSignupEmail, etSignupPassword;
    MaterialButton btnSignup, btnGotoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        initializeFirebase();
        initialize();

        btnGotoSignup.setOnClickListener(view -> {
            clLogin.setVisibility(View.GONE);
            clSignup.setVisibility(View.VISIBLE);
        });

        btnGotoLogin.setOnClickListener(view -> {
            clLogin.setVisibility(View.VISIBLE);
            clSignup.setVisibility(View.GONE);
        });

        btnLogin.setOnClickListener(view -> login());

        btnSignup.setOnClickListener(view -> signup());

        btnSkipAuth.setOnClickListener(view -> {
            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
            Toast.makeText(this, "Welcome to Rent A Van", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void initialize() {
        clLogo = findViewById(R.id.clLogo);
        clLogin = findViewById(R.id.clLogin);
        clSignup = findViewById(R.id.clSignup);
        // log in
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGotoSignup = findViewById(R.id.btnGotoSignup);
        btnSkipAuth = findViewById(R.id.btnSkipAuth);
        // sign up
        etSignupFirstName = findViewById(R.id.etSignupFirstName);
        etSignupLastName = findViewById(R.id.etSignupLastName);
        etSignupMobile = findViewById(R.id.etSignupMobile);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnGotoLogin = findViewById(R.id.btnGotoLogin);
    }

    private void login() {
        btnLogin.setEnabled(false);
        String email = Objects.requireNonNull(etLoginEmail.getText()).toString();
        String password = Objects.requireNonNull(etLoginPassword.getText()).toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Utils.basicDialog(this, "All fields are required!", "Okay");
            btnLogin.setEnabled(true);
            return;
        }

        AUTH.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DatabaseReference dbUserType = RENTAVAN.getReference("user_"+ Objects.requireNonNull(AUTH.getCurrentUser()).getUid()).child("userType");
                        dbUserType.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int userType = Integer.parseInt(snapshot.getValue().toString());
                                Utils.Cache.setInt(AuthenticationActivity.this, "user_type", userType);

                                Toast.makeText(AuthenticationActivity.this, "Signed in as "+email, Toast.LENGTH_SHORT).show();

                                if (userType == 0) {
                                    startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                                    finish();
                                }
                                else if (userType == 1) {
                                    startActivity(new Intent(AuthenticationActivity.this, AdminActivity.class));
                                    finish();
                                }
                                else {
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else {
                        Utils.basicDialog(this, "Email or password is incorrect", "Try again");
                        btnLogin.setEnabled(true);
                    }
                });
    }

    private void signup() {
        btnSignup.setEnabled(false);
        String firstName = Objects.requireNonNull(etSignupFirstName.getText()).toString();
        String lastName = Objects.requireNonNull(etSignupLastName.getText()).toString();
        String mobile = Objects.requireNonNull(etSignupMobile.getText()).toString();
        String email = Objects.requireNonNull(etSignupEmail.getText()).toString();
        String password = Objects.requireNonNull(etSignupPassword.getText()).toString();

        if (TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(mobile) ||
                TextUtils.isEmpty(password)) {
            Utils.basicDialog(this, "All fields are required!", "Okay");
            btnSignup.setEnabled(true);
            return;
        }

        if (password.length() < 6) {
            Utils.basicDialog(this, "The password should be at least 6 characters", "Okay");
            btnSignup.setEnabled(true);
            return;
        }

        AUTH.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dbUser = RENTAVAN.getReference("user_"+ Objects.requireNonNull(AUTH.getCurrentUser()).getUid());
                        dbUser.child("uid").setValue(AUTH.getCurrentUser().getUid());
                        dbUser.child("firstName").setValue(firstName);
                        dbUser.child("lastName").setValue(lastName);
                        dbUser.child("mobile").setValue(mobile);
                        dbUser.child("userType").setValue(0);
                        Toast.makeText(AuthenticationActivity.this, "Creating account...", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Utils.basicDialog(this, "Something went wrong! Please try again.", "Try again");
                        btnSignup.setEnabled(true);
                    }
                });
    }
}