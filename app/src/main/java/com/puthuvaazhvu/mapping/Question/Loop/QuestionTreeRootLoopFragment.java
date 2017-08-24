package com.puthuvaazhvu.mapping.Question.Loop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.R;

/**
 * Created by muthuveerappans on 8/25/17.
 */

//TODO
public class QuestionTreeRootLoopFragment extends Fragment {
    QuestionModal questionModal;

    public static QuestionTreeRootLoopFragment getInstance(QuestionModal questionModal) {
        QuestionTreeRootLoopFragment questionTreeRootLoopFragment = new QuestionTreeRootLoopFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question_data", questionModal);

        questionTreeRootLoopFragment.setArguments(bundle);

        return questionTreeRootLoopFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_frame_for_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        questionModal = getArguments().getParcelable("question_data");
    }
}
