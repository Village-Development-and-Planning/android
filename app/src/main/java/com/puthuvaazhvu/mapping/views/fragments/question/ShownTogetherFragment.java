package com.puthuvaazhvu.mapping.views.fragments.question;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.views.custom_components.LinearLayoutRecyclerView;
import com.puthuvaazhvu.mapping.views.fragments.options.CreateOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactoryWithNonScrollableCheckableUI;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.GridQuestionFragmentCommunication;
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
        dataList.addAll(getQuestion().getLatestAnswer().getChildren());
        // add a valid answer to avoid locking in the same question
        getQuestion().getLatestAnswer().setOptions(Question.noDataWithValidOptions());
        //addDummyAnswersToQuestionTree(dataList, getQuestion());
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

//        optionsRecyclerView = view.findViewById(R.id.together_question_recycler_view);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        optionsRecyclerView.setLayoutManager(linearLayoutManager);
//
//        optionsRecyclerView.setAdapter(shownTogetherAdapter);

        String questionText = getQuestion().getTextForLanguage();
        String rawNumber = getQuestion().getRawNumber();

        String text = rawNumber + ". " + questionText;
        getQuestionText().setText(text);
    }

    @Override
    public void onBackButtonPressed(View view) {
        showTogetherQuestionCommunication.onBackPressedFromShownTogetherQuestion(getQuestion());
    }

    @Override
    public void onNextButtonPressed(View view) {
        updateAnswersInQuestionTreeWithReadAnswers();
        showTogetherQuestionCommunication.onNextPressedFromShownTogetherQuestion(getQuestion());
    }

    private void updateAnswersInQuestionTreeWithReadAnswers() {
        for (Question question : dataList) {
            OptionsUI optionsUI = optionsUiObjects.get(question.getRawNumber());
            if (optionsUI != null && optionsUI.response() != null) {
//                Answer answer = new Answer(optionsUI.response(), question);
//                question.setAnswer(answer);
                question.getLatestAnswer().setOptions(optionsUI.response());
            }
        }
    }

//    private void addDummyAnswersToQuestionTree(ArrayList<Question> data, Question node) {
//        if (node.getAnswers().isEmpty()) {
//            Answer dummyAnswer = new Answer(Question.noDataWithValidOptions(), node);
//            node.setAnswer(dummyAnswer);
//        }
//
//        for (Question child : node.getLatestAnswer().getChildren()) {
//            data.add(child);
//            addDummyAnswersToQuestionTree(data, child);
//        }
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

            optionsUiObjects.put(question.getRawNumber(), optionsUI);

            questionTextView.setText(question.getTextForLanguage());
        }
    }
}
