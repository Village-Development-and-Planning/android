package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.util.ArrayList;

import okhttp3.internal.Util;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class GPSOptionsUI extends OptionsUI
        implements View.OnClickListener {

    protected Button button;
    private final AppCompatActivity activity;
    private Location lastLocation;
    private TextView textView;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    public GPSOptionsUI(ViewGroup frame, Context context, Question question) {
        super(frame, context, question);
        activity = (AppCompatActivity) context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationRequest();
        startLocationUpdates();

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
    public View createView() {
        View view = inflateView(R.layout.gps_options);
        button = view.findViewById(R.id.button);
        button.setText(context.getString(R.string.press));
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(this);

        textView = view.findViewById(R.id.location_text);

        if (getLatestOptions() != null) {
            Option lo = getLatestOptions().get(0);
            textView.setText(lo.getTextString());
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            getLastLocation();
        }
    }

    @Override
    public ArrayList<Option> response() {
        if (lastLocation == null) {
            return null;
        }

        stopLocationUpdates();

        ArrayList<Option> options = new ArrayList<>();

        String loc = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
        options.add(new Option("", "GPS", new Text("", loc, loc, ""), "", ""));

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
                            Utils.showMessageToast(R.string.gps_connection_err, context);
                        }
                    }
                });
    }

    private void updateUI() {
        textView.setText("" + lastLocation.getLatitude() + ", " + "" + lastLocation.getLongitude());
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

    private void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        } catch (SecurityException e) {
            Timber.e(e.getLocalizedMessage());
        }
    }
}
