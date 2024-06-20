package com.rentavan.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentavan.R;
import com.rentavan.adapters.BookingAdapter;
import com.rentavan.objects.Booking;
import com.rentavan.utils.Utils;

import java.util.ArrayList;

public class BookingsFragment extends Fragment {

    FirebaseDatabase RENTAVAN;
    FirebaseUser USER;
    ValueEventListener velBookings;

    private void initializeFirebase() {
        USER = FirebaseAuth.getInstance().getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    ArrayList<Booking> arrBooking;
    ArrayList<String> arrBookingReferences;
    BookingAdapter bookingAdapter;

    RecyclerView rvBooking;
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
        view = inflater.inflate(R.layout.fragment_bookings, container, false);

        initialize();
        loadBooking();

        return view;
    }

    private void loadBooking() {
        if (userType == 0) { // passenger
            loadBookingsUsingUserReference();
        }
        else if (userType == 1) { // admin
            loadBookings();
        }
    }

    private void loadBookingsUsingUserReference() {
        arrBooking = new ArrayList<>();
        arrBookingReferences = new ArrayList<>();
        rvBooking = view.findViewById(R.id.rvBookings);
        loadingBar = view.findViewById(R.id.loadingBar);
        rvBooking.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference dbBookingReferences = RENTAVAN.getReference("user_"+USER.getUid()+"_bookings");
        dbBookingReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrBooking.clear();
                arrBookingReferences.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvBooking.setVisibility(View.INVISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvBooking.setVisibility(View.VISIBLE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String bookingUid = dataSnapshot.getValue().toString();
                    arrBookingReferences.add(bookingUid);
                }

                for (int i = 0; i < arrBookingReferences.size(); i++) {
                    String bookingUid = arrBookingReferences.get(i);
                    DatabaseReference dbBooking = RENTAVAN.getReference("bookings/"+bookingUid);
                    dbBooking.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Booking booking = snapshot.getValue(Booking.class);
                            arrBooking.add(booking);
                            bookingAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                loadingBar.hide();

                bookingAdapter = new BookingAdapter(getContext(), arrBooking);
                rvBooking.setAdapter(bookingAdapter);
                bookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBookings() {
        arrBooking = new ArrayList<>();
        rvBooking = view.findViewById(R.id.rvBookings);
        loadingBar = view.findViewById(R.id.loadingBar);
        rvBooking.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference dbBooking = RENTAVAN.getReference("bookings");
        velBookings = dbBooking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrBooking.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvBooking.setVisibility(View.INVISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvBooking.setVisibility(View.VISIBLE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    arrBooking.add(booking);
                }

                loadingBar.hide();

                bookingAdapter = new BookingAdapter(getContext(), arrBooking);
                rvBooking.setAdapter(bookingAdapter);
                bookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize() {
        tvEmpty = view.findViewById(R.id.tvEmpty);
        loadingBar = view.findViewById(R.id.loadingBar);

        userType = Utils.Cache.getInt(requireContext(), "user_type");
    }
}