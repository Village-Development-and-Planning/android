package com.puthuvaazhvu.mapping.views.fragments.summary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 10/20/17.
 */

public class SummaryFragment extends Fragment {
    private Survey survey;

    public static SummaryFragment getInstance(Survey survey) {
        SummaryFragment summaryFragment = new SummaryFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", survey);
        summaryFragment.setArguments(bundle);

        return summaryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        survey = getArguments().getParcelable("data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }
}
