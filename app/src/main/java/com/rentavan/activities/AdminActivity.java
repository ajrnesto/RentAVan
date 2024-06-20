package com.rentavan.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.rentavan.R;
import com.rentavan.fragments.BookingsFragment;
import com.rentavan.fragments.ChatFragment;
import com.rentavan.fragments.InboxFragment;
import com.rentavan.fragments.ProfileFragment;
import com.rentavan.utils.Utils;

public class AdminActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_admin);

        initializeFirebase();
        initialize();
        backstackListener();
        bottom_navbar.setOnItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.miProfile:
                    if (bottom_navbar.getSelectedItemId() != R.id.miProfile) {
                        Fragment profileFragment = new ProfileFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, profileFragment, "PROFILE_FRAGMENT");
                        fragmentTransaction.addToBackStack("PROFILE_FRAGMENT");
                        fragmentTransaction.commit();
                    }
                    break;
                case R.id.miChat:
                    if (bottom_navbar.getSelectedItemId() != R.id.miChat) {
                        if (Utils.Cache.getInt(AdminActivity.this, "user_type") == 0) {
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
                    break;
                case R.id.miBookings:
                    Fragment bookingsFragment = new BookingsFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, bookingsFragment, "BOOKINGS_FRAGMENT");
                    fragmentTransaction.addToBackStack("BOOKINGS_FRAGMENT");
                    fragmentTransaction.commit();
                    break;
            }
            return true;
        });
    }

    private void initialize() {
        bottom_navbar = findViewById(R.id.bottom_navbar_admin);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        Fragment startBookingsFragment = new BookingsFragment();
        fragmentTransaction.replace(R.id.fragmentHolder, startBookingsFragment, "BOOKINGS_FRAGMENT");
        fragmentTransaction.addToBackStack("BOOKINGS_FRAGMENT");
        fragmentTransaction.commit();
        bottom_navbar.getMenu().getItem(2).setChecked(true);
    }

    private void backstackListener() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("PROFILE_FRAGMENT");
            InboxFragment inboxFragment = (InboxFragment) getSupportFragmentManager().findFragmentByTag("INBOX_FRAGMENT");
            ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag("CHAT_FRAGMENT");
            BookingsFragment bookingsFragment = (BookingsFragment) getSupportFragmentManager().findFragmentByTag("BOOKINGS_FRAGMENT");

            if (profileFragment != null && profileFragment.isVisible()) {
                bottom_navbar.getMenu().getItem(0).setChecked(true);
            }
            else if (inboxFragment != null && inboxFragment.isVisible()) {
                softKeyboardListener();
                bottom_navbar.getMenu().getItem(1).setChecked(true);
            }
            else if (chatFragment != null && chatFragment.isVisible()) {
                softKeyboardListener();
                bottom_navbar.getMenu().getItem(1).setChecked(true);
            }
            else if (bookingsFragment != null && bookingsFragment.isVisible()) {
                bottom_navbar.getMenu().getItem(2).setChecked(true);
            }
        });
    }

    private void softKeyboardListener() {
        final View activityRootView = findViewById(R.id.fragmentHolder);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > Utils.dpToPx(AdminActivity.this, 200)) {
                // if keyboard visible
                bottom_navbar.setVisibility(View.GONE);
            }
            else {
                bottom_navbar.setVisibility(View.VISIBLE);
            }
        });
    }
}