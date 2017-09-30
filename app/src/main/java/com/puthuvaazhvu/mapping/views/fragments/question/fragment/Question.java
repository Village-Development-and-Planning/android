package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public abstract class Question extends Fragment {
    protected FragmentCommunicationInterface communicationInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicationInterface = (FragmentCommunicationInterface) context;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Please implement the " + FragmentCommunicationInterface.class.getSimpleName() + " on the parent ativity");
        }
    }

    @Nullable
    @Override
    public abstract View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public void sendQuestionToCaller(Data data) {
        communicationInterface.onQuestionAnswered(data);
    }

    public void backButtonPressedInsideQuestion(Data data) {
        communicationInterface.onBackPressedFromQuestion(data);
    }
}
