package com.rentavan.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentavan.R;
import com.rentavan.fragments.BookingsFragment;
import com.rentavan.fragments.ChatFragment;
import com.rentavan.fragments.FleetFragment;
import com.rentavan.fragments.InboxFragment;
import com.rentavan.fragments.NewBookingFragment;
import com.rentavan.fragments.ProfileFragment;
import com.rentavan.utils.Utils;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase RENTAVAN;
    FirebaseUser USER;

    private void initializeFirebase() {
        USER = FirebaseAuth.getInstance().getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    BottomNavigationView bottom_navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebase();
        initialize();
        backstackListener();

        bottom_navbar.setOnItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.miProfile:
                    if (USER != null) {
                        if (bottom_navbar.getSelectedItemId() != R.id.miProfile) {
                            Fragment profileFragment = new ProfileFragment();
                            fragmentTransaction.replace(R.id.fragmentHolder, profileFragment, "PROFILE_FRAGMENT");
                            fragmentTransaction.addToBackStack("PROFILE_FRAGMENT");
                            fragmentTransaction.commit();
                        }
                    }
                    else {
                        Utils.loginRequiredDialog(MainActivity.this, bottom_navbar, "Sign in to Rent A Van to manage your profile.");
                    }
                    break;
                case R.id.miVans:
                    if (bottom_navbar.getSelectedItemId() != R.id.miVans) {
                        Fragment fleetFragment = new FleetFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, fleetFragment, "FLEET_FRAGMENT");
                        fragmentTransaction.addToBackStack("FLEET_FRAGMENT");
                        fragmentTransaction.commit();
                    }
                    break;
                case R.id.miChat:
                    if (USER != null) {
                        if (bottom_navbar.getSelectedItemId() != R.id.miChat) {
                            if (Utils.Cache.getInt(MainActivity.this, "user_type") == 0) {
                                Fragment chatFragment = new ChatFragment();
                                fragmentTransaction.replace(R.id.fragmentHolder, chatFragment, "CHAT_FRAGMENT");
                                fragmentTransaction.addToBackStack("CHAT_FRAGMENT");
                                fragmentTransaction.commit();
                            }
                            else {
                                Fragment inboxFragment = new InboxFragment();
                                fragmentTransaction.replace(R.id.fragmentHolder, inboxFragment, "INBOX_FRAGMENT");
                                fragmentTransaction.addToBackStack("INBOX_FRAGMENT");
                                fragmentTransaction.commit();
                            }
                        }
                    }
                    else {
                        Utils.loginRequiredDialog(MainActivity.this, bottom_navbar, "Sign in to Rent A Van to contact our support.");
                    }
                    break;
                case R.id.miBookings:
                    if (USER != null) {
                        Fragment bookingsFragment = new BookingsFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, bookingsFragment, "BOOKINGS_FRAGMENT");
                        fragmentTransaction.addToBackStack("BOOKINGS_FRAGMENT");
                        fragmentTransaction.commit();
                    }
                    else {
                        Utils.loginRequiredDialog(MainActivity.this, bottom_navbar, "Sign in to Rent A Van to manage your bookings.");
                    }
                    break;
            }
            return true;
        });
    }

    private void initialize() {
        bottom_navbar = findViewById(R.id.bottom_navbar);

        if (Utils.Cache.getInt(MainActivity.this, "user_type") == 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            Fragment startFleetFragment = new FleetFragment();
            fragmentTransaction.replace(R.id.fragmentHolder, startFleetFragment, "FLEET_FRAGMENT");
            fragmentTransaction.addToBackStack("FLEET_FRAGMENT");
            fragmentTransaction.commit();
            bottom_navbar.getMenu().getItem(1).setChecked(true);
        }
        else if (Utils.Cache.getInt(MainActivity.this, "user_type") == 2) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            Fragment startBookingsFragment = new BookingsFragment();
            fragmentTransaction.replace(R.id.fragmentHolder, startBookingsFragment, "BOOKINGS_FRAGMENT");
            fragmentTransaction.addToBackStack("BOOKINGS_FRAGMENT");
            fragmentTransaction.commit();
            bottom_navbar.getMenu().getItem(1).setChecked(true);
        }
    }

    private void backstackListener() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("PROFILE_FRAGMENT");
            FleetFragment fleetFragment = (FleetFragment) getSupportFragmentManager().findFragmentByTag("FLEET_FRAGMENT");
            InboxFragment inboxFragment = (InboxFragment) getSupportFragmentManager().findFragmentByTag("INBOX_FRAGMENT");
            ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag("CHAT_FRAGMENT");
            BookingsFragment bookingsFragment = (BookingsFragment) getSupportFragmentManager().findFragmentByTag("BOOKINGS_FRAGMENT");
            NewBookingFragment newBookingFragment = (NewBookingFragment) getSupportFragmentManager().findFragmentByTag("NEW_BOOKING_FRAGMENT");

            if (profileFragment != null && profileFragment.isVisible()) {
                bottom_navbar.getMenu().getItem(0).setChecked(true);
            }
            else if (fleetFragment != null && fleetFragment.isVisible()) {
                bottom_navbar.getMenu().getItem(1).setChecked(true);
            }
            else if (newBookingFragment != null && newBookingFragment.isVisible()) {
                bottom_navbar.getMenu().getItem(1).setChecked(true);
            }
            else if (inboxFragment != null && inboxFragment.isVisible()) {
                softKeyboardListener();
                bottom_navbar.getMenu().getItem(2).setChecked(true);
            }
            else if (chatFragment != null && chatFragment.isVisible()) {
                softKeyboardListener();
                bottom_navbar.getMenu().getItem(2).setChecked(true);
            }
            else if (bookingsFragment != null && bookingsFragment.isVisible()) {
                bottom_navbar.getMenu().getItem(3).setChecked(true);
            }
        });
    }

    private void softKeyboardListener() {
        final View activityRootView = findViewById(R.id.fragmentHolder);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > Utils.dpToPx(MainActivity.this, 200)) {
                // if keyboard visible
                bottom_navbar.setVisibility(View.GONE);
            }
            else {
                bottom_navbar.setVisibility(View.VISIBLE);
            }
        });
    }
}