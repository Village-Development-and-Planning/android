package com.puthuvaazhvu.mapping.views.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.application.modal.ApplicationData;

/**
 * Created by muthuveerappans on 11/12/17.
 */

public class DataFragment extends Fragment {
    private ApplicationData applicationData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public ApplicationData getApplicationData() {
        return applicationData;
    }

    public void setApplicationData(ApplicationData applicationData) {
        this.applicationData = applicationData;
    }
}
