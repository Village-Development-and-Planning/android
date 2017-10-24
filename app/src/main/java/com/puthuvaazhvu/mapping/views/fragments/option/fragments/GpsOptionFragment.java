package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.GpsAnswerData;

import timber.log.Timber;

import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class GpsOptionFragment extends ButtonOptionsFragment
        implements View.OnClickListener
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , DialogInterface.OnClickListener
        , LocationListener {
    private Button button;
    private OptionData optionData;
    private double lat, lng;
    private Location lastLocation;

    private GoogleApiClient googleApiClient;
    private boolean gpsAvailable = false;

    private LocationRequest locationRequest;

    public static GpsOptionFragment getInstance(OptionData optionData) {
        GpsOptionFragment gpsOptionFragment = new GpsOptionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("optionData", optionData);
        gpsOptionFragment.setArguments(bundle);
        return gpsOptionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleApiClient == null) {
            createGooglePlayServices();
        }

        if (locationRequest == null) {
            locationRequest = createLocationRequest();
        }
    }

    @Override
    public void onStart() {
        connectGooglePlayServices();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        disconnectGooglePlayServices();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void createGooglePlayServices() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void connectGooglePlayServices() {
        googleApiClient.connect();
    }

    private void disconnectGooglePlayServices() {
        googleApiClient.disconnect();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        optionData = getArguments().getParcelable("optionData");
        button = getButton();
        button.setText(getString(R.string.press));
        button.setVisibility(View.GONE);
    }

    @Override
    public OptionData getUpdatedData() {
        GpsAnswerData gpsAnswer = new GpsAnswerData(optionData.getQuestionID(), optionData.getQuestionText(), lat, lng);
        optionData.setAnswerData(gpsAnswer);
        return optionData;
    }

    @Override
    public void onButtonClick(View view) {
        if (checkGpsPermissionGranted()) {
            if (gpsAvailable) {
                Location location = getLocation();
                if (location != null) {
                    lastLocation = location;
                    Utils.showMessageToast(R.string.gps_recorded_successfully, getContext());
                    setLocation();
                } else {
                    Timber.e("Location is null.");
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        gpsAvailable = true;

        button.setVisibility(View.VISIBLE);

        startLocationUpdates();

        Timber.i("Gps connected. Ready to record the location.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        gpsAvailable = false;

        String errorMessage = getString(R.string.gps_connection_err);

        switch (i) {
            case CAUSE_NETWORK_LOST:
                Timber.e("Gps connection failed with message : peer device connection was lost");
                errorMessage = "Peer device connection was lost";
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Timber.e("A suspension cause informing that the service has been killed.");
                errorMessage = "Gps service has been killed";
                break;
        }

        showErrorDialog(getString(R.string.gps_error_title), errorMessage, null, getString(R.string.retry), this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        gpsAvailable = false;

        String errorMessage = connectionResult.getErrorMessage();
        int errorCode = connectionResult.getErrorCode();

        Timber.e("GPS connection failed with message : " + errorMessage + " states code: " + errorCode);

        showErrorDialog(getString(R.string.gps_error_title), errorMessage, null, getString(R.string.retry), this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        setLocation();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_NEGATIVE:
                connectGooglePlayServices();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PermissionRequestCodes.REQUEST_GPS_CODE:
                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // We can now safely use the API we requested access to
                    gpsAvailable = true;
                    Location location = getLocation();
                    if (location != null) {
                        lastLocation = location;
                        Utils.showMessageToast(R.string.gps_recorded_successfully, getContext());
                        setLocation();
                    } else {
                        Timber.e("Location is null.");
                    }

                } else {

                    // Permission was denied or request was cancelled
                    gpsAvailable = false;
                    Timber.e("Error in permission for GPS");
                    checkGpsPermissionGranted();
                }
                break;
        }
    }

    /**
     * @return True if the permission has been granted
     */
    private boolean checkGpsPermissionGranted() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PermissionRequestCodes.REQUEST_GPS_CODE
            );
            return false;
        } else {
            Timber.i("GPS permission granted");
            return true;
        }
    }

    private Location getLocation() {
        try {
            return LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
        } catch (SecurityException e) {
            Timber.e("Error in permission for GPS : " + e.getMessage());
        }
        return null;
    }

    private void setLocation() {
        lat = lastLocation.getLatitude();
        lng = lastLocation.getLongitude();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        } catch (SecurityException e) {
            Timber.e("Error in permission for GPS : " + e.getMessage());
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }
}
