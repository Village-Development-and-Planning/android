package org.ptracking.vdp.views.fragments.question;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.modals.Text;
import org.ptracking.vdp.utils.Utils;
import org.ptracking.vdp.views.fragments.question.types.QuestionFragmentTypes;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 23/01/18.
 */

public class GPSQuestionFragment extends QuestionFragment {
    private static final float LOCATION_MIN_ACCURACY = 50; //meters
    private static final float LOCATION_ACCURACY_COUNT_MAX = 10;

    protected Button button;
    private Location lastLocation;
    private Location finalLocation;
    private TextView textView;

    private FusedLocationProviderClient mFusedLocationClient;

    private float lowestLocationAccuracy = Float.MAX_VALUE;
    private float locationCount;

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
                if (lastLocation != null) {
                    updateUI();
                    checkForLocationAccuracy(lastLocation);
                }
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup optionsContainer = view.findViewById(R.id.options_container);

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

        disableUI();

        updateUIWithCachedOptions(currentQuestion);
    }

    @Override
    public void onBackButtonPressed(View view) {
        callbacks.onBackPressed(QuestionFragmentTypes.GPS);
    }

    @Override
    public void onNextButtonPressed(View view) {
        ArrayList<Option> options = response();
        if (options == null) {
            callbacks.onError(Utils.getErrorMessage(R.string.options_not_entered_err, getContext()));
        } else {
            callbacks.onNextPressed(QuestionFragmentTypes.GPS, options);
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

    private void updateUIWithCachedOptions(Question question) {
        if (question.getCurrentAnswer() != null
                && question.getCurrentAnswer().isDummy())
            return;

        Answer answer = question.getCurrentAnswer();
        if (answer != null) {
            ArrayList<Option> currOpt = answer.getLoggedOptions();
            if (currOpt != null && currOpt.size() > 0) {
                Option lo = currOpt.get(0);
                textView.setText(lo.getTextString());
            }
        }
    }

    public ArrayList<Option> response() {
        if (finalLocation == null) {
            return null;
        }

        stopLocationUpdates();

        ArrayList<Option> options = new ArrayList<>();

        String loc = finalLocation.getLatitude() + "," + finalLocation.getLongitude();
        Option option = new Option(
                "GPS",
                new Text(loc, loc),
                "GPS"
        );
        option.setValue(loc);
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
                            if (lastLocation != null) {
                                updateUI();
                                checkForLocationAccuracy(lastLocation);
                            }
                            //Utils.showMessageToast(R.string.gps_recorded_successfully, context);
                        } else {
                            Timber.e(task.getException());
                            Utils.showMessageToast(R.string.gps_connection_err, getContext());
                        }
                    }
                });
    }

    private void checkForLocationAccuracy(Location lastLocation) {
        locationCount++;
        if (lastLocation.hasAccuracy() && lastLocation.getAccuracy() < lowestLocationAccuracy) {
            lowestLocationAccuracy = lastLocation.getAccuracy();
        }

        if (lowestLocationAccuracy <= LOCATION_MIN_ACCURACY) {
            finalLocation = lastLocation;
            locationCount = 0;
            enableUI();
        } else if (locationCount > LOCATION_ACCURACY_COUNT_MAX) {
            finalLocation = lastLocation;
            enableUI();
        }
    }

    private void updateUI() {
        if (textView == null || lastLocation == null) return;
        textView.setText("" + lastLocation.getLatitude() + ", " + "" + lastLocation.getLongitude() + "\n" +
                "Time: " + DateFormat.getTimeInstance().format(new Date()));
    }

    private void disableUI() {
        getNextButton().setVisibility(View.INVISIBLE);
    }

    private void enableUI() {
        getNextButton().setVisibility(View.VISIBLE);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
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
