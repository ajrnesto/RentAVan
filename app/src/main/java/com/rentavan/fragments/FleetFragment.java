package com.rentavan.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentavan.R;
import com.rentavan.adapters.FleetAdapter;
import com.rentavan.objects.Van;
import com.rentavan.utils.Utils;

import java.util.ArrayList;

public class FleetFragment extends Fragment {

    FirebaseDatabase RENTAVAN;
    FirebaseUser USER;
    ValueEventListener velInbox;

    private void initializeFirebase() {
        USER = FirebaseAuth.getInstance().getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    ArrayList<Van> arrFleet;
    FleetAdapter fleetAdapter;

    View view;
    RecyclerView rvFleet;
    CircularProgressIndicator loadingBar;

    int userType = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fleet, container, false);

        initializeFirebase();
        initialize();
        loadFleet();

        return view;
    }

    private void initialize() {
        loadingBar = view.findViewById(R.id.loadingBar);

        userType = Utils.Cache.getInt(requireContext(), "user_type");
    }

    private void loadFleet() {
        arrFleet = new ArrayList<>();
        rvFleet = view.findViewById(R.id.rvFleet);
        loadingBar = view.findViewById(R.id.loadingBar);
        rvFleet.setLayoutManager(new LinearLayoutManager(getContext()));
        arrFleet.clear();

        arrFleet.add(new Van("Toyota GL Grandia",
                4000,
                "Joshua dela Cruz",
                "Diesel",
                "Auxiliary Cable",
                12,
                3,
                R.drawable.van_toyota_gl_grandia));
        arrFleet.add(new Van("Toyota Super Grandia Elite 2.8",
                4500,
                "Paul Garcia",
                "Diesel",
                "Bluetooth",
                10,
                5,
                R.drawable.van_super_grandia_elite));
        arrFleet.add(new Van("Maxus V80 2022",
                4000,
                "Christian Reyes",
                "Diesel",
                "Auxiliary Cable",
                13,
                0,
                R.drawable.van_maxus_v80));
        arrFleet.add(new Van("Foton Transvan",
                5000,
                "Justine Ramos",
                "Diesel",
                "Bluetooth",
                15,
                2,
                R.drawable.van_foton_transvan));
        arrFleet.add(new Van("Maxus G10",
                4000,
                "Mark Mendoza",
                "Diesel",
                "Bluetooth",
                9,
                1,
                R.drawable.van_maxus_g10));
        arrFleet.add(new Van("Gazelle Next Van",
                8000,
                "John Lloyd Santos",
                "Diesel",
                "Bluetooth",
                19,
                7,
                R.drawable.van_gazelle_next_van));
        arrFleet.add(new Van("Foton Traveller",
                7000,
                "Jerome Flores",
                "Diesel",
                "Bluetooth",
                16,
                2,
                R.drawable.van_foton_traveller));
        arrFleet.add(new Van("Foton Toano",
                7000,
                "Adrian Gonzales",
                "Diesel",
                "Auxiliary Cable",
                15,
                2,
                R.drawable.van_foton_toano));

        loadingBar.hide();
        fleetAdapter = new FleetAdapter(getContext(), arrFleet);
        rvFleet.setAdapter(fleetAdapter);
        fleetAdapter.notifyDataSetChanged();

        /*
    String model;
    int price;
    String driver;
    String fuelType;
    String audioSystem;
    int seats;
    int luggage;
    ;*/
    }
}