package com.airport.flightsschedule.flightstatus.utils;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airport.flightsschedule.flightstatus.activities.MainActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;

public class CheckLocationEnabled {

    private static LocationManager locationManager;
    private static LocationRequest locationRequest;
    private static LocationCallback locationCallback;
    private static LocationSettingsRequest mLocationSettingsRequest;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    };
    private AppCompatActivity activity;
    private SettingsClient mSettingsClient;
    private FusedLocationProviderClient locationProviderClient;

    public static CheckLocationEnabled init(MainActivity activity) {
        CheckLocationEnabled locationEnabled = new CheckLocationEnabled();
        locationEnabled.activity = activity;
        locationEnabled.mSettingsClient = LocationServices.getSettingsClient(activity);
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();
        builder.setAlwaysShow(true);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

            }
        };
        return locationEnabled;
    }

    public void turnGPSOn(final onGpsListener onGpsListener) {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (onGpsListener != null) {
                onGpsListener.gpsStatus(true);
            }
        } else {
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(activity, locationSettingsResponse -> {
                if (onGpsListener != null) {
                    onGpsListener.gpsStatus(true);
                }
            }).addOnFailureListener(activity, e -> {
                try {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            int permissionLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
                            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                locationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
                                locationProviderClient.getLastLocation().addOnCompleteListener(task -> requestLocationUpdate());
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            ResolvableApiException rae = (ResolvableApiException) e;
                            try {
                                rae.startResolutionForResult(activity, 98);
                            } catch (IntentSender.SendIntentException | RuntimeException ex) {
                                ex.printStackTrace();
                            }
                            break;
                    }
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    public boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            if (lm != null) {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!network_enabled) {
            // notify user
            turnGPSOn(isGPSEnable -> {

            });
            return false;
        } else {
            return true;
        }
    }

    public void requestLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnCompleteListener(task -> {
                });
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnCompleteListener(task -> {
            });
        }
    }

    public interface onGpsListener {
        void gpsStatus(boolean isGPSEnable);
    }
}