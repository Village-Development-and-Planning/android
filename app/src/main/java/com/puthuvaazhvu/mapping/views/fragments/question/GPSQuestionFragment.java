package com.puthuvaazhvu.mapping.views.fragments.question;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 23/01/18.
 */

public class GPSQuestionFragment extends SingleQuestionFragmentBase {
    protected Button button;
    private Location lastLocation;
    private TextView textView;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private SettingsClient mSettingsClient;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mSettingsClient = LocationServices.getSettingsClient(getContext());

        createLocationRequest();
        buildLocationSettingsRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    lastLocation = location;
                }
                if (lastLocation != null) updateUI();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View gpsUI = getLayoutInflater().inflate(R.layout.gps_options, optionsContainer, true);
        button = gpsUI.findViewById(R.id.button);
        button.setText(getString(R.string.press));
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

        textView = view.findViewById(R.id.location_text);

        if (getCurrentAnswerOptions() != null) {
            Option lo = getCurrentAnswerOptions().get(0);
            textView.setText(lo.getTextString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Timber.i("User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Timber.i("User chose not to make required location settings changes.");
                        updateUI();
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackButtonPressed(View view) {
        getSingleQuestionFragmentCommunication().onBackPressedFromSingleQuestion(getQuestion());
    }

    @Override
    public void onNextButtonPressed(View view) {
        ArrayList<Option> options = response();
        if (options == null) {
            getSingleQuestionFragmentCommunication().onError(Utils.getErrorMessage(R.string.options_not_entered_err
                    , getContext()));
        } else {
            getSingleQuestionFragmentCommunication().onNextPressedFromSingleQuestion(getQuestion(), options);
        }
    }

    @Override
    public OptionsUIFactory getOptionsUIFactory() {
        return null;
    }

    protected ArrayList<Option> getCurrentAnswerOptions() {
        if (getQuestion().getCurrentAnswer() != null
                && getQuestion().getCurrentAnswer().isDummy())
            return null;

        Answer answer = getQuestion().getCurrentAnswer();
        if (answer != null) {
            ArrayList<Option> currOpt = answer.getLoggedOptions();
            if (currOpt != null && currOpt.size() > 0) {
                return currOpt;
            }
        }
        return null;
    }

    public ArrayList<Option> response() {
        if (lastLocation == null) {
            return null;
        }

        stopLocationUpdates();

        ArrayList<Option> options = new ArrayList<>();

        String loc = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
        Option option = new Option(
                "GPS",
                new Text(loc, loc),
                "GPS"
        );
        options.add(option);

        return options;
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();
                            if (lastLocation != null) updateUI();
                            //Utils.showMessageToast(R.string.gps_recorded_successfully, context);
                        } else {
                            Timber.e(task.getException());
                            Utils.showMessageToast(R.string.gps_connection_err, getContext());
                        }
                    }
                });
    }

    private void updateUI() {
        if (textView == null || lastLocation == null) return;
        textView.setText("" + lastLocation.getLatitude() + ", " + "" + lastLocation.getLongitude() + "\n" +
                "Time: " + DateFormat.getTimeInstance().format(new Date()));
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Timber.i("All location settings are satisfied.");

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Timber.i("Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Timber.i("PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Timber.e(errorMessage);
                        }
                    }
                });
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
}
