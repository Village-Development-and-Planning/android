package com.puthuvaazhvu.mapping.views.fragments.question;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.custom_components.LinearLayoutRecyclerView;
import com.puthuvaazhvu.mapping.views.fragments.options.CreateOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactoryWithNonScrollableCheckableUI;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ShowTogetherQuestionCommunication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by muthuveerappans on 1/10/18.
 */

public class ShownTogetherFragment extends QuestionDataFragment {
    private ArrayList<Question> dataList;
    //private RecyclerView optionsRecyclerView;
    private LinearLayoutRecyclerView together_question_container;
    private ShownTogetherAdapter shownTogetherAdapter;
    private HashMap<String, OptionsUI> optionsUiObjects;

    protected ShowTogetherQuestionCommunication showTogetherQuestionCommunication;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            showTogetherQuestionCommunication = (ShowTogetherQuestionCommunication) context;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Please implement the " + ShowTogetherQuestionCommunication.class.getSimpleName() + " on the parent ativity");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = new ArrayList<>();
        dataList.addAll(getQuestion().getCurrentAnswer().getChildren());

        getQuestion().getCurrentAnswer().setDummy(false);

        shownTogetherAdapter = new ShownTogetherAdapter();
        optionsUiObjects = new HashMap<>();
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
        together_question_container.setAdapter(shownTogetherAdapter);

        String questionText = getQuestion().getTextString();
        String rawNumber = getQuestion().getNumber();

        String text = rawNumber + ". " + questionText;
        getQuestionText().setText(text);
    }

    @Override
    public void onBackButtonPressed(View view) {
        showTogetherQuestionCommunication.onBackPressedFromShownTogetherQuestion(getQuestion());
    }

    @Override
    public void onNextButtonPressed(View view) {
        //updateAnswersInQuestionTreeWithReadAnswers();

        if (!updateAnswersInQuestionTreeWithReadAnswers()) {
            Utils.showMessageToast(R.string.options_not_entered_err, getContext());
            return;
        }

        showTogetherQuestionCommunication.onNextPressedFromShownTogetherQuestion(getQuestion());
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

                baseQuestionFragmentCommunication.getFlowLogic().setCurrent(question);
                baseQuestionFragmentCommunication.getFlowLogic().update(optionsUI.response());
            } else {
                return false;
            }
        }

        // reset to the parent question for seamless flow.
        baseQuestionFragmentCommunication.getFlowLogic().setCurrent(getQuestion());

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
