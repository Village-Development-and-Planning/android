package com.puthuvaazhvu.mapping.views.activities.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.puthuvaazhvu.mapping.modals.Question;

/**
 * Created by muthuveerappans on 12/21/17.
 */

public class DataFragment extends Fragment {
    private Question currentQuestion;
    private String snapshot;

    public static DataFragment getInstance(FragmentManager fm) {
        DataFragment dataFragment = (DataFragment) fm.findFragmentByTag("data_fragment");
        if (dataFragment == null) {
            dataFragment = new DataFragment();

            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(dataFragment, "data_fragment");
            fragmentTransaction.commitAllowingStateLoss();
        }
        return dataFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }
}
