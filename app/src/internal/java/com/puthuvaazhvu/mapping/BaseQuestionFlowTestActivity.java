package com.puthuvaazhvu.mapping;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;

/**
 * Created by muthuveerappans on 22/02/18.
 */

public class BaseQuestionFlowTestActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.frame_created_in_java);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));

        sharedPreferences = getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);

        setContentView(frameLayout);
    }

    void loadFragment(FlowLogic.FlowData flowData) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), flowData.getFragment());
        fragmentTransaction.commit();
    }
}
