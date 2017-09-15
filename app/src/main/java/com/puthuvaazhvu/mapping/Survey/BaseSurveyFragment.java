package com.puthuvaazhvu.mapping.Survey;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by muthuveerappans on 9/15/17.
 */

public class BaseSurveyFragment extends Fragment {
    protected SurveyActivityUICommunicationInterface parentCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            parentCallback = (SurveyActivityUICommunicationInterface) context;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The activity must implement " + SurveyActivityUICommunicationInterface.class.getSimpleName() + " inorder to work.");
        }
    }

    public SurveyActivityUICommunicationInterface getParentCallback() {
        return parentCallback;
    }
}
