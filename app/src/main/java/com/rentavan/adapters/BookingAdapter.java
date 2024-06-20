package com.rentavan.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentavan.R;
import com.rentavan.activities.MainActivity;
import com.rentavan.fragments.NewBookingFragment;
import com.rentavan.objects.Booking;
import com.rentavan.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.bookingViewHolder>{

    FirebaseDatabase RENTAVAN = FirebaseDatabase.getInstance();
    FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();

    Context context;
    ArrayList<Booking> arrBookings = new ArrayList<>();

    public BookingAdapter(Context context, ArrayList<Booking> arrBookings) {
        this.context = context;
        this.arrBookings = arrBookings;
    }

    @NonNull
    @Override
    public bookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_booking, parent, false);
        return new bookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookingViewHolder holder, int position) {
        Booking bookings = arrBookings.get(position);

        loadPassengerName(holder, bookings.getPassengerUid());
        holder.tvModel.setText(bookings.getModel());
        holder.tvTripName.setText(bookings.getTripName());
        holder.tvTravelPurpose.setText(bookings.getTravelPurpose());
        try {
            loadRoute(holder, bookings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadDuration(holder, bookings);

        if (bookings.getStatus() == 0) { // if pending
            if (Utils.Cache.getInt(context, "user_type") == 0) { // if regular user
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnComplete.setVisibility(View.GONE);
            }
            else if (Utils.Cache.getInt(context, "user_type") == 1) { // if admin
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnComplete.setVisibility(View.GONE);
            }
        }
        else if (bookings.getStatus() == 1) { // if accepted
            if (Utils.Cache.getInt(context, "user_type") == 0) { // if regular user
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnComplete.setVisibility(View.GONE);
            }
            else if (Utils.Cache.getInt(context, "user_type") == 1) { // if admin
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnComplete.setVisibility(View.VISIBLE);
            }
        }
        else if (bookings.getStatus() == 2) { // if completed
            if (Utils.Cache.getInt(context, "user_type") == 0) { // if regular user
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnComplete.setVisibility(View.GONE);
            }
            else if (Utils.Cache.getInt(context, "user_type") == 1) { // if admin
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnComplete.setVisibility(View.GONE);
            }
        }
    }

    private void loadDuration(bookingViewHolder holder, Booking bookings) {
        long start = bookings.getDateStart();
        long end = bookings.getDateEnd();
        long time = bookings.getTime();

        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");

        holder.tvDuration.setText(sdfDate.format(start) + " - " + sdfDate.format(end));
        holder.tvTime.setText(sdfTime.format(time));
    }

    private void loadRoute(bookingViewHolder holder, Booking bookings) throws IOException {
        double latitudeStart = bookings.getPickUpLatitude();
        double longitudeStart = bookings.getPickUpLongitude();
        double latitudeEnd = bookings.getDestinationLatitude();
        double longitudeEnd = bookings.getDestinationLongitude();

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressesStart = geocoder.getFromLocation(latitudeStart, longitudeStart, 1);
        List<Address> addressesEnd = geocoder.getFromLocation(latitudeEnd, longitudeEnd, 1);
        Address addressStart = addressesStart.get(0);
        Address addressEnd = addressesEnd.get(0);

        String localityStart = addressStart.getLocality();
        if (localityStart == null) {
            localityStart = "";
        }
        String localityEnd = addressEnd.getLocality();
        if (localityEnd == null) {
            localityEnd = "";
        }

        String subAdminAreaStart = addressStart.getSubAdminArea();
        if (subAdminAreaStart == null) {
            subAdminAreaStart = "";
        }
        String subAdminAreaEnd = addressEnd.getSubAdminArea();
        if (subAdminAreaEnd == null) {
            subAdminAreaEnd = "";
        }

        String start = Utils.addressBuilder(localityStart, subAdminAreaStart);
        String end = Utils.addressBuilder(localityEnd, subAdminAreaEnd);
        //holder.tvRoute.setText("From: "+start+" To: "+end);
        holder.tvRoute.setText(start+" to "+end);
    }

    private void loadPassengerName(bookingViewHolder holder, String passengerUid) {
        DatabaseReference dbPassenger = RENTAVAN.getReference("user_"+passengerUid);
        dbPassenger.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue().toString();
                String lastName = snapshot.child("lastName").getValue().toString();

                holder.tvPassenger.setText(firstName + " " + lastName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrBookings.size();
    }

    public class bookingViewHolder extends RecyclerView.ViewHolder{
        TextView tvPassenger, tvModel, tvTripName, tvTravelPurpose, tvRoute, tvDuration, tvTime;
        MaterialButton btnCancel, btnAccept, btnDelete, btnComplete;

        public bookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPassenger = itemView.findViewById(R.id.tvPassenger);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            tvTravelPurpose = itemView.findViewById(R.id.tvTravelPurpose);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnComplete = itemView.findViewById(R.id.btnComplete);

            btnCancel.setOnClickListener(view -> {
                Booking booking = arrBookings.get(getAdapterPosition());
                String bookingUid = booking.getUid();

                // remove booking
                DatabaseReference dbBooking = RENTAVAN.getReference("bookings/"+bookingUid);
                dbBooking.removeValue();

                // remove user's booking reference
                DatabaseReference dbUserBooking = RENTAVAN.getReference("user_"+USER.getUid()+"_bookings/"+bookingUid);
                dbUserBooking.removeValue();
            });

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Booking booking = arrBookings.get(getAdapterPosition());
                    String bookingUid = booking.getUid();

                    // update booking status from pending to accepted
                    DatabaseReference dbBookingStatus = RENTAVAN.getReference("bookings/"+bookingUid+"/status");
                    dbBookingStatus.setValue(1);
                }
            });

            btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Booking booking = arrBookings.get(getAdapterPosition());
                    String bookingUid = booking.getUid();

                    // update booking status from pending to accepted
                    DatabaseReference dbBookingStatus = RENTAVAN.getReference("bookings/"+bookingUid+"/status");
                    dbBookingStatus.setValue(2);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Booking booking = arrBookings.get(getAdapterPosition());
                    String bookingUid = booking.getUid();

                    // remove booking
                    DatabaseReference dbBooking = RENTAVAN.getReference("bookings/"+bookingUid);
                    dbBooking.removeValue();

                    // remove user's booking reference
                    DatabaseReference dbUserBooking = RENTAVAN.getReference("user_"+USER.getUid()+"_bookings/"+bookingUid);
                    dbUserBooking.removeValue();
                }
            });
        }
    }
}