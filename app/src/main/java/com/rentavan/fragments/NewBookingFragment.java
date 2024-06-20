package com.rentavan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rentavan.R;
import com.rentavan.activities.LocationSelectionActivity;
import com.rentavan.objects.Booking;
import com.rentavan.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class NewBookingFragment extends Fragment {

    FirebaseDatabase RENTAVAN;
    FirebaseUser USER;

    private void initializeFirebase() {
        USER = FirebaseAuth.getInstance().getCurrentUser();
        RENTAVAN = FirebaseDatabase.getInstance();
    }

    View view;
    TextView tvModel, tvDriver;
    TextInputEditText etTripName, etPurpose, etPassengers,
            etPickupLocation, etDestinationLocation,
            etDateStart, etDateEnd, etTime, etNote;
    MaterialButton btnBook;

    // top action bar elements
    MaterialButton btnBack;
    TextView tvActivityTitle;

    // builder date picker
    MaterialDatePicker.Builder<Long> builderDateStart;
    MaterialDatePicker.Builder<Long> builderDateEnd;
    MaterialDatePicker<Long> datePickerStart;
    MaterialDatePicker<Long> datePickerEnd;

    // time picker
    MaterialTimePicker timePicker;
    long timePickerGetSelection = 0;

    int userType = 0;
    String model, driver, fuelType, audioSystem;
    int price, seats, luggage, imageId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_booking, container, false);

        initializeFirebase();
        initialize();
        initializeTopActionBar();
        initializeDatePicker();
        loadSelectedVanAndDriver();
        initializeTimePicker();

        etPickupLocation.setOnClickListener(view1 -> {
            Intent iLocationSelection = new Intent(requireContext(), LocationSelectionActivity.class);
            iLocationSelection.putExtra("action", "PICK_UP");
            startActivity(iLocationSelection);
        });

        etDestinationLocation.setOnClickListener(view1 -> {
            Intent iLocationSelection = new Intent(requireContext(), LocationSelectionActivity.class);
            iLocationSelection.putExtra("action", "DESTINATION");
            startActivity(iLocationSelection);
        });

        etDateStart.setOnClickListener(view -> {
            etDateStart.setEnabled(false);
            datePickerStart.show(getParentFragmentManager(), "START_DATE_PICKER");
        });

        etDateEnd.setOnClickListener(view -> {
            etDateEnd.setEnabled(false);
            datePickerEnd.show(getParentFragmentManager(), "END_DATE_PICKER");
        });

        etTime.setOnClickListener(view -> {
            etTime.setEnabled(false);
            timePicker.show(getParentFragmentManager(), "TIME_PICKER");
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitBooking();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // pick up
        Utils.Cache.removeKey(requireContext(), "pick_up_latitude");
        Utils.Cache.removeKey(requireContext(), "pick_up_longitude");
        Utils.Cache.removeKey(requireContext(), "pick_up_locality");
        Utils.Cache.removeKey(requireContext(), "pick_up_subAdminArea");

        // destination
        Utils.Cache.removeKey(requireContext(), "destination_latitude");
        Utils.Cache.removeKey(requireContext(), "destination_longitude");
        Utils.Cache.removeKey(requireContext(), "destination_locality");
        Utils.Cache.removeKey(requireContext(), "destination_subAdminArea");
    }

    @Override
    public void onResume() {
        super.onResume();

        // pick up
        double pickupLatitude = Utils.Cache.getDouble(requireContext(), "pick_up_latitude");
        double pickupLongitude = Utils.Cache.getDouble(requireContext(), "pick_up_longitude");
        String pickupLocality = Utils.Cache.getString(requireContext(), "pick_up_locality");
        String pickupSubAdminArea = Utils.Cache.getString(requireContext(), "pick_up_subAdminArea");

        // destination
        double destinationLatitude = Utils.Cache.getDouble(requireContext(), "destination_latitude");
        double destinationLongitude = Utils.Cache.getDouble(requireContext(), "destination_longitude");
        String destinationLocality = Utils.Cache.getString(requireContext(), "destination_locality");
        String destinationSubAdminArea = Utils.Cache.getString(requireContext(), "destination_subAdminArea");

        if ((pickupLatitude != 0 || pickupLongitude != 0)) {
            etPickupLocation.setText(Utils.addressBuilder(pickupLocality, pickupSubAdminArea));
        }
        else {
            Objects.requireNonNull(etPickupLocation.getText()).clear();
        }

        if ((destinationLatitude != 0 || destinationLongitude != 0)) {
            etDestinationLocation.setText(Utils.addressBuilder(destinationLocality, destinationSubAdminArea));
        }
        else {
            Objects.requireNonNull(etDestinationLocation.getText()).clear();
        }
    }

    private void initialize() {
        userType = Utils.Cache.getInt(requireContext(), "user_type");

        tvModel = view.findViewById(R.id.tvModel);
        tvDriver = view.findViewById(R.id.tvDriver);
        etTripName = view.findViewById(R.id.etTripName);
        etPurpose = view.findViewById(R.id.etPurpose);
        etPassengers = view.findViewById(R.id.etPassengers);
        etPickupLocation = view.findViewById(R.id.etPickupLocation);
        etDestinationLocation = view.findViewById(R.id.etDestinationLocation);
        etDateStart = view.findViewById(R.id.etDateStart);
        etDateEnd = view.findViewById(R.id.etDateEnd);
        etTime = view.findViewById(R.id.etTime);
        etNote = view.findViewById(R.id.etNote);
        btnBook = view.findViewById(R.id.btnBook);

        Bundle bookingArgs = getArguments();
        model = bookingArgs.getString("model");
        price = bookingArgs.getInt("price");
        driver = bookingArgs.getString("driver");
        fuelType = bookingArgs.getString("fuel_type");
        audioSystem = bookingArgs.getString("audio_system");
        seats = bookingArgs.getInt("seats");
        luggage = bookingArgs.getInt("luggage");
        imageId = bookingArgs.getInt("image_dd");
    }

    private void initializeTopActionBar() {
        tvActivityTitle = view.findViewById(R.id.tvActivityTitle);
        btnBack = view.findViewById(R.id.btnActionBar);

        tvActivityTitle.setText("Booking Application Form");

        btnBack.setOnClickListener(view1 -> {
            requireActivity().onBackPressed();
        });
    }

    private void initializeDatePicker() {
        // start date
        builderDateStart = MaterialDatePicker.Builder.datePicker();
        builderDateStart.setTitleText("Start Date")
                .setSelection(System.currentTimeMillis());
        datePickerStart = builderDateStart.build();
        datePickerStart.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            etDateStart.setText(sdf.format(datePickerStart.getSelection()));
            etDateStart.setEnabled(true);
        });
        datePickerStart.addOnNegativeButtonClickListener(view -> {
            etDateStart.setEnabled(true);
        });
        datePickerStart.addOnCancelListener(dialogInterface -> {
            etDateStart.setEnabled(true);
        });

        // end date
        builderDateEnd = MaterialDatePicker.Builder.datePicker();
        builderDateEnd.setTitleText("End Date")
                .setSelection(System.currentTimeMillis());
        datePickerEnd = builderDateEnd.build();
        datePickerEnd.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            etDateEnd.setText(sdf.format(datePickerEnd.getSelection()));
            etDateEnd.setEnabled(true);
        });
        datePickerEnd.addOnNegativeButtonClickListener(view -> {
            etDateEnd.setEnabled(true);
        });
        datePickerEnd.addOnCancelListener(dialogInterface -> {
            etDateEnd.setEnabled(true);
        });
    }

    private void initializeTimePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Pick up time")
                .setTheme(R.style.RentAVan_TimePicker)
                .build();
        timePicker.addOnPositiveButtonClickListener(view -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            timePickerGetSelection = calendar.getTimeInMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            etTime.setText(sdf.format(timePickerGetSelection));
            etTime.setEnabled(true);
        });
        timePicker.addOnNegativeButtonClickListener(view -> {
            etTime.setEnabled(true);
        });
        timePicker.addOnCancelListener(dialogInterface -> {
            etTime.setEnabled(true);
        });
    }

    private void loadSelectedVanAndDriver() {
        tvModel.setText(model);
        tvDriver.setText(driver);
    }

    private void submitBooking() {
        // retrieve form data
        String model = tvModel.getText().toString();
        String tripName = etTripName.getText().toString().trim();
        String travelPurpose = etPurpose.getText().toString().trim();
        int passengers = Integer.parseInt(etPassengers.getText().toString().trim());
        double pickUpLatitude = Utils.Cache.getDouble(requireContext(), "pick_up_latitude");
        double pickUpLongitude = Utils.Cache.getDouble(requireContext(), "pick_up_longitude");
        double destinationLatitude = Utils.Cache.getDouble(requireContext(), "destination_latitude");
        double destinationLongitude = Utils.Cache.getDouble(requireContext(), "destination_longitude");
        long dateStart = datePickerStart.getSelection();
        long dateEnd = datePickerEnd.getSelection();
        long time = timePickerGetSelection;
        String note = etNote.getText().toString().trim();

        // validate form
        if (tripName.isEmpty() || travelPurpose.isEmpty()) {
            Utils.basicDialog(requireContext(), "Trip name and purpose are required", "Okay");
            return;
        }

        if (pickUpLatitude == 0 || destinationLatitude == 0) {
            Utils.basicDialog(requireContext(), "Please specify your pick up location and destination", "Okay");
            return;
        }

        if (dateStart < System.currentTimeMillis()) {
            Utils.basicDialog(requireContext(), "Invalid starting date selected", "Okay");
            return;
        }

        if (dateStart < System.currentTimeMillis()) {
            Utils.basicDialog(requireContext(), "Invalid ending date selected", "Okay");
            return;
        }

        // submit booking
        DatabaseReference dbWorkshopBooking = RENTAVAN.getReference("bookings");
        String bookingUid = dbWorkshopBooking.push().getKey();
        Booking newBooking = new Booking(bookingUid,
                USER.getUid(),
                model,
                tripName,
                travelPurpose,
                passengers,
                pickUpLatitude,
                pickUpLongitude,
                destinationLatitude,
                destinationLongitude,
                dateStart,
                dateEnd,
                time,
                note,
                0);
        dbWorkshopBooking.child(Objects.requireNonNull(bookingUid)).setValue(newBooking);
        // save reference for user
        DatabaseReference dbUserBooking = RENTAVAN.getReference("user_"+Objects.requireNonNull(USER).getUid()+"_bookings").child(bookingUid);
        dbUserBooking.setValue(bookingUid);

        Fragment bookingsFragment = new BookingsFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, bookingsFragment, "BOOKINGS_FRAGMENT");
        fragmentTransaction.addToBackStack("BOOKINGS_FRAGMENT");
        fragmentTransaction.commit();
    }
}