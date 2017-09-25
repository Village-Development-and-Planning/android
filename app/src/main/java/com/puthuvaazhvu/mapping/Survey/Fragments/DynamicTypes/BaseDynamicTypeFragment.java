package com.puthuvaazhvu.mapping.Survey.Fragments.DynamicTypes;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by muthuveerappans on 9/21/17.
 */

/*
Base template for Fragments containing questions.
 */
public abstract class BaseDynamicTypeFragment extends Fragment implements View.OnClickListener {
    protected DynamicFragmentTypeCommunicationInterface dynamicFragmentTypeCommunicationInterface;

    public void setDynamicFragmentTypeCommunicationInterface(DynamicFragmentTypeCommunicationInterface dynamicFragmentTypeCommunicationInterface) {
        this.dynamicFragmentTypeCommunicationInterface = dynamicFragmentTypeCommunicationInterface;
    }

    public DynamicFragmentTypeCommunicationInterface getDynamicFragmentTypeCommunicationInterface() {
        return dynamicFragmentTypeCommunicationInterface;
    }
}
