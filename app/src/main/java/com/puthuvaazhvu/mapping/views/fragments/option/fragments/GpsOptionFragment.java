package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.GpsAnswer;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class GpsOptionFragment extends ButtonOptionsFragment implements View.OnClickListener {
    private Button button;
    private Data data;
    private long lat, lng;

    public static GpsOptionFragment getInstance(Data data) {
        GpsOptionFragment gpsOptionFragment = new GpsOptionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        gpsOptionFragment.setArguments(bundle);
        return gpsOptionFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        data = getArguments().getParcelable("data");
        button = getButton();
    }

    @Override
    public Data getUpdatedData() {
        GpsAnswer gpsAnswer = new GpsAnswer(data.getQuestionID(), data.getQuestionText(), lat, lng);
        data.setAnswer(gpsAnswer);
        return data;
    }

    @Override
    public void onButtonClick(View view) {
        // Todo: recode gps
    }
}
