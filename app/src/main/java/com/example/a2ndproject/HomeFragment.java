package com.example.a2ndproject;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView home_txt_location;
    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;

    private final String[] permissions = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        home_txt_location = view.findViewById(R.id.home_text_location);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        requestLocationPermissions();

        return view;
    }

    private void requestLocationPermissions() {
        // Check if permissions are already granted
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            startLocationUpdates();
        } else {
            requestPermissionLauncher.launch(permissions);
        }
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), permissionsGranted -> {
                boolean allPermissionsGranted = true;
                for (Boolean granted : permissionsGranted.values()) {
                    if (!granted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    getCurrentLocation();
                    startLocationUpdates();
                } else {
                    // Permission denied
                    home_txt_location.setText("Permission denied for location");
                }
            });

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    updateLocationText(location);
                } else {
                    home_txt_location.setText("Location not available");
                }
            }
        });
    }

    private void updateLocationText(Location location) {
        if (location != null) {
            getAddressFromLocation(location);
        } else {
            home_txt_location.setText("Location not available");
        }
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String street = address.getAddressLine(0);
                home_txt_location.setText(street);
            } else {
                home_txt_location.setText("Address not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            home_txt_location.setText("Error getting address");
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocationText(location);
                }
            }
        };

        // Request location updates
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop location updates when fragment is no longer visible
        if (client != null && locationCallback != null) {
            client.removeLocationUpdates(locationCallback);
        }
    }
}
