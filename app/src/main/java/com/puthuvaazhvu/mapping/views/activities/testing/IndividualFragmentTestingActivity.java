package com.puthuvaazhvu.mapping.views.activities.testing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;

/**
 * Created by muthuveerappans on 11/7/17.
 */

public abstract class IndividualFragmentTestingActivity extends MenuActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_individual_fragment);

        addFragment();
    }

    private void addFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, getFragment());
        fragmentTransaction.commit();
    }

    public abstract Fragment getFragment();
}
