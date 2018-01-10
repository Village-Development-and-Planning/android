package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;
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
    private Location location;
    private boolean gpsOK;
    private TextView textView;

    private FusedLocationProviderClient mFusedLocationClient;

    public GPSOptionsUI(ViewGroup frame, Context context) {
        super(frame, context);
        activity = (AppCompatActivity) context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public View createView() {
        View view = inflateView(R.layout.gps_options);
        button = view.findViewById(R.id.button);
        button.setText(context.getString(R.string.press));
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(this);

        textView = view.findViewById(R.id.location_text);
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
        if (location == null) {
            return null;
        }

        ArrayList<Option> options = new ArrayList<>();

        String loc = location.getLatitude() + "," + location.getLongitude();
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
                            location = task.getResult();

                            Utils.showMessageToast(R.string.gps_recorded_successfully, context);
                            textView.setText("" + location.getLatitude() + ", " + "" + location.getLongitude());
                        } else {
                            Timber.e(task.getException());
                            Utils.showMessageToast(R.string.gps_connection_err, context);
                        }
                    }
                });
    }
}
