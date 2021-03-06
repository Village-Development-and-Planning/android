package org.ptracking.vdp.views.fragments.question;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.utils.Utils;
import org.ptracking.vdp.views.activities.main.MainActivityViewModal;
import org.ptracking.vdp.views.custom_components.LinearLayoutRecyclerView;
import org.ptracking.vdp.views.fragments.options.CreateOptionsUI;
import org.ptracking.vdp.views.fragments.options.OptionsUI;
import org.ptracking.vdp.views.fragments.options.factory.OptionsUIFactoryWithNonScrollableCheckableUI;
import org.ptracking.vdp.views.fragments.question.types.QuestionFragmentTypes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by muthuveerappans on 1/10/18.
 */

public class ShownTogetherFragment extends QuestionFragment {
    private ArrayList<Question> dataList;

    private LinearLayoutRecyclerView together_question_container;

    private ShownTogetherAdapter shownTogetherAdapter;

    private HashMap<String, OptionsUI> optionsUiObjects;

    private MainActivityViewModal viewModal;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        viewModal = ViewModelProviders.of(getActivity()).get(MainActivityViewModal.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.together_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        together_question_container = view.findViewById(R.id.together_question_container);

        dataList = new ArrayList<>();
        dataList.addAll(currentQuestion.getCurrentAnswer().getChildren());

        currentQuestion.getCurrentAnswer().setDummy(false);

        shownTogetherAdapter = new ShownTogetherAdapter();
        optionsUiObjects = new HashMap<>();

        together_question_container.setAdapter(shownTogetherAdapter);
    }

    @Override
    public void onBackButtonPressed(View view) {
        callbacks.onBackPressed(QuestionFragmentTypes.SHOWN_TOGETHER);
    }

    @Override
    public void onNextButtonPressed(View view) {
        if (!updateAnswersInQuestionTreeWithReadAnswers()) {
            Utils.showMessageToast(R.string.options_not_entered_err, getContext());
            return;
        }

        callbacks.onNextPressed(QuestionFragmentTypes.SHOWN_TOGETHER, null);
    }

    private boolean updateAnswersInQuestionTreeWithReadAnswers() {
        for (Question question : dataList) {
            OptionsUI optionsUI = optionsUiObjects.get(question.getNumber());
            if (optionsUI != null && optionsUI.response() != null) {
                if (question.getAnswers().isEmpty()) {
                    // add dummy answer if empty
                    question.addAnswer(
                            Answer.createDummyAnswer(question)
                    );
                }

                viewModal.getFlowLogic().setCurrent(question);
                viewModal.getFlowLogic().update(optionsUI.response());
            } else {
                return false;
            }
        }

        // reset to the parent question for seamless flow.
        viewModal.getFlowLogic().setCurrent(currentQuestion);

        return true;
    }

//    private boolean checkIfAllQuestionAreAnswered() {
//        for (Question child : getQuestion().getCurrentAnswer().getChildren()) {
//            if (child.getAnswers().isEmpty()) return false;
//        }
//
//        return true;
//    }

    private class ShownTogetherAdapter extends RecyclerView.Adapter<ShownTogetherVH> {

        @Override
        public ShownTogetherVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.together_question_adapter, parent, false);
            return new ShownTogetherVH(view);
        }

        @Override
        public void onBindViewHolder(ShownTogetherVH holder, int position) {
            holder.populateViews(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    private class ShownTogetherVH extends RecyclerView.ViewHolder {
        private FrameLayout childOptionsContainer;
        private TextView questionTextView;

        public ShownTogetherVH(View itemView) {
            super(itemView);

            childOptionsContainer = itemView.findViewById(R.id.child_options_frame);
            questionTextView = itemView.findViewById(R.id.q_text);
        }

        public void populateViews(Question question) {
            childOptionsContainer.removeAllViews();

            CreateOptionsUI createOptionsUI = new CreateOptionsUI(question);
            OptionsUI optionsUI = createOptionsUI
                    .createOptionsUI(new OptionsUIFactoryWithNonScrollableCheckableUI(question, childOptionsContainer));
            optionsUI.attachToRoot();

            optionsUiObjects.put(question.getNumber(), optionsUI);

            questionTextView.setText(question.getTextString());
        }
    }
}
