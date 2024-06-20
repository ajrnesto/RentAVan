package com.rentavan.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.rentavan.R;
import com.rentavan.fragments.LocationSelectionFragment;

public class LocationSelectionActivity extends AppCompatActivity {

    private static final FirebaseDatabase FIXCARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();

    BottomNavigationView bottom_navbar;

    // top action bar elements
    MaterialButton btnBack;
    TextView tvActivityTitle;

    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);

        initialize();

        Bundle locationSelectionArgs = getIntent().getExtras();
        locationSelectionArgs.putString("action", action);

        Fragment locationSelectionMapFragment = new LocationSelectionFragment();
        locationSelectionMapFragment.setArguments(locationSelectionArgs);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mapContainer, locationSelectionMapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initialize() {
        bottom_navbar = findViewById(R.id.bottom_navbar);

        action = getIntent().getStringExtra("action");
    }
}