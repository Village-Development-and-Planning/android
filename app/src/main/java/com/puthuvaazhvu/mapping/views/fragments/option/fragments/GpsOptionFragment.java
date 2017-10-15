package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.GpsAnswerData;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class GpsOptionFragment extends ButtonOptionsFragment implements View.OnClickListener {
    private Button button;
    private OptionData optionData;
    private long lat, lng;

    public static GpsOptionFragment getInstance(OptionData optionData) {
        GpsOptionFragment gpsOptionFragment = new GpsOptionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("optionData", optionData);
        gpsOptionFragment.setArguments(bundle);
        return gpsOptionFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        optionData = getArguments().getParcelable("optionData");
        button = getButton();
    }

    @Override
    public OptionData getUpdatedData() {
        GpsAnswerData gpsAnswer = new GpsAnswerData(optionData.getQuestionID(), optionData.getQuestionText(), lat, lng);
        optionData.setAnswerData(gpsAnswer);
        return optionData;
    }

    @Override
    public void onButtonClick(View view) {
        // Todo: recode gps
    }
}
