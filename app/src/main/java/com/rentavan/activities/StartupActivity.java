package com.rentavan.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
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

public class StartupActivity extends AppCompatActivity {

    FirebaseDatabase RENTAVAN;
    FirebaseUser USER;

    private void initializeFirebase() {
        USER = FirebaseAuth.getInstance().getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    ConstraintLayout clLogo, clButtons;
    MaterialButton btnGetStarted;

    Animation animIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        initializeFirebase();
        initialize();
        handleAnimations();

        clLogo.startAnimation(animIntro);

        btnGetStarted.setOnClickListener(view -> {
            startActivity(new Intent(StartupActivity.this, AuthenticationActivity.class));
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
    }

    private void initialize() {
        clLogo = findViewById(R.id.clLogo);
        clButtons = findViewById(R.id.clButtons);
        btnGetStarted = findViewById(R.id.btnGetStarted);

        animIntro = AnimationUtils.loadAnimation(this, R.anim.intro);
    }

    private void handleAnimations() {
        animIntro.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    if (USER != null) {
                        DatabaseReference dbUserType = RENTAVAN.getReference("user_"+ Objects.requireNonNull(USER).getUid()).child("userType");
                        dbUserType.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int userType = Integer.parseInt(snapshot.getValue().toString());
                                Utils.Cache.setInt(StartupActivity.this, "user_type", userType);

                                Toast.makeText(StartupActivity.this, "Signed in as "+USER.getEmail(), Toast.LENGTH_SHORT).show();

                                if (userType == 0) {
                                    startActivity(new Intent(StartupActivity.this, MainActivity.class));
                                    finish();
                                }
                                else if (userType == 1) {
                                    startActivity(new Intent(StartupActivity.this, AdminActivity.class));
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
                        startActivity(new Intent(StartupActivity.this, MainActivity.class));
                        finish();
                        // clButtons.setVisibility(View.VISIBLE);
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}