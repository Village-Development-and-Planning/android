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

public class GpsOption extends Options implements View.OnClickListener {
    private Button button;
    private Data data;
    private long lat, lng;

    public static GpsOption getInstance(Data data) {
        GpsOption gpsOption = new GpsOption();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        gpsOption.setArguments(bundle);
        return gpsOption;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.options_button, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        data = getArguments().getParcelable("data");
        button = view.findViewById(R.id.button);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            // Todo: recode gps
        }
    }

    @Override
    public Data getUpdatedData() {
        GpsAnswer gpsAnswer = new GpsAnswer(data.getQuestionID(), data.getQuestionText(), lat, lng);
        data.setAnswer(gpsAnswer);
        return data;
    }
}
