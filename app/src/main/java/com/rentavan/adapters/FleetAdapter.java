package com.rentavan.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rentavan.R;
import com.rentavan.activities.AuthenticationActivity;
import com.rentavan.activities.MainActivity;
import com.rentavan.fragments.NewBookingFragment;
import com.rentavan.objects.Van;
import com.rentavan.utils.Utils;

import java.util.ArrayList;

public class FleetAdapter extends RecyclerView.Adapter<FleetAdapter.fleetViewHolder>{

    FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();

    Context context;
    ArrayList<Van> arrFleet = new ArrayList<>();

    public FleetAdapter(Context context, ArrayList<Van> arrFleet) {
        this.context = context;
        this.arrFleet = arrFleet;
    }

    @NonNull
    @Override
    public fleetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_van, parent, false);
        return new fleetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull fleetViewHolder holder, int position) {
        Van van = arrFleet.get(position);

        holder.tvModel.setText(van.getModel());
        holder.tvPrice.setText("₱"+van.getPrice()+".00/day");
        holder.tvDriver.setText(van.getDriver());
        holder.tvFuelType.setText(van.getFuelType());
        holder.tvAudioSystem.setText(van.getAudioSystem());
        holder.tvSeats.setText(van.getSeats()+" Seats");
        holder.tvLuggage.setText(van.getLuggage() + " Luggage");
        holder.imgVan.setImageResource(van.getImageId());
    }

    @Override
    public int getItemCount() {
        return arrFleet.size();
    }

    public class fleetViewHolder extends RecyclerView.ViewHolder{
        AppCompatImageView imgVan;
        TextView tvModel, tvPrice, tvDriver, tvFuelType, tvAudioSystem, tvSeats, tvLuggage;
        MaterialButton btnBook;

        public fleetViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVan = itemView.findViewById(R.id.imgVan);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            tvFuelType = itemView.findViewById(R.id.tvFuelType);
            tvAudioSystem = itemView.findViewById(R.id.tvAudioSystem);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvLuggage = itemView.findViewById(R.id.tvLuggage);
            btnBook = itemView.findViewById(R.id.btnBook);

            btnBook.setOnClickListener(view -> {
                Van van = arrFleet.get(getAdapterPosition());
                String model = van.getModel();
                int price = van.getPrice();
                String driver = van.getDriver();
                String fuelType = van.getFuelType();
                String audioSystem = van.getAudioSystem();
                int seats = van.getSeats();
                int luggage = van.getLuggage();
                int imageId = van.getImageId();

                Bundle bookingArgs = new Bundle();
                bookingArgs.putString("model", model);
                bookingArgs.putInt("price", price);
                bookingArgs.putString("driver", driver);
                bookingArgs.putString("fuel_type", fuelType);
                bookingArgs.putString("audio_system", audioSystem);
                bookingArgs.putInt("seats", seats);
                bookingArgs.putInt("luggage", luggage);
                bookingArgs.putInt("image_id", imageId);

                if (USER != null) {
                    Fragment newBookingFragment = new NewBookingFragment();
                    newBookingFragment.setArguments(bookingArgs);
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, newBookingFragment, "NEW_BOOKING_FRAGMENT");
                    fragmentTransaction.addToBackStack("NEW_BOOKING_FRAGMENT");
                    fragmentTransaction.commit();
                }
                else {
                    MaterialAlertDialogBuilder dialogLoginRequired = new MaterialAlertDialogBuilder(context);
                    dialogLoginRequired.setTitle("Sign in required");
                    dialogLoginRequired.setMessage("Sign in to Rent A Van to book for trips.");
                    dialogLoginRequired.setPositiveButton("Log in", (dialogInterface, i) -> {
                        context.startActivity(new Intent(context, AuthenticationActivity.class));
                        ((Activity)context).finish();
                    });
                    dialogLoginRequired.setNeutralButton("Back", (dialogInterface, i) -> { });
                    dialogLoginRequired.setOnDismissListener(dialogInterface -> {
                    });
                    dialogLoginRequired.show();
                }
            });
        }
    }
}