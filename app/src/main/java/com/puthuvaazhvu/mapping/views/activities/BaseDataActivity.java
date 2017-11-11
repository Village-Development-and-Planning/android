package com.puthuvaazhvu.mapping.views.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.application.modal.ApplicationData;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 11/11/17.
 */

public class BaseDataActivity extends BaseActivity {

    private DataFragment dataFragment;
    protected ApplicationData applicationData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        dataFragment = (DataFragment) fm.findFragmentByTag("data_fragment");

        if (dataFragment == null) {
            dataFragment = new DataFragment();
            fm.beginTransaction().add(dataFragment, "data_fragment").commit();
            dataFragment.setApplicationData(MappingApplication.applicationData);
        }

        applicationData = dataFragment.getApplicationData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataFragment.setApplicationData(applicationData);
    }
}
