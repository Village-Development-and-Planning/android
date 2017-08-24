package com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class RootQuestionsGridHolderFragment extends Fragment {
    RecyclerView recyclerView;
    List<QuestionModal> questionModalList;
    RootQuestionsHolderGridFragmentCommunicationInterface communicationInterface;
    QuestionsAdapter questionsAdapter;

    public static RootQuestionsGridHolderFragment getInstance(ArrayList<QuestionModal> questionModalList) {
        RootQuestionsGridHolderFragment rootQuestionsGridHolderFragment = new RootQuestionsGridHolderFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("question_data_list", questionModalList);

        rootQuestionsGridHolderFragment.setArguments(bundle);

        return rootQuestionsGridHolderFragment;
    }

    public void setCommunicationInterface(RootQuestionsHolderGridFragmentCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.questions_grid_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        questionModalList = getArguments().getParcelableArrayList("question_data_list");

        recyclerView = view.findViewById(R.id.grid_questions_recycler_view);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int viewWidth = recyclerView.getMeasuredWidth();
                        float cardViewWidth = getActivity().getResources()
                                .getDimension(R.dimen.tag_question_card_side_length);
                        int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                        gridLayoutManager.setSpanCount(newSpanCount);
                        gridLayoutManager.requestLayout();
                    }
                });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext()
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                QuestionModal questionModal = questionModalList.get(position);
                sendDataToCaller(questionModal);
            }
        }));

        questionsAdapter = new QuestionsAdapter();
        recyclerView.setAdapter(questionsAdapter);
    }

    private void sendDataToCaller(QuestionModal questionModal) {
        if (communicationInterface != null) {
            communicationInterface.onSelectedQuestion(questionModal);
        } else {
            throw new RuntimeException("The interface is null on " + RootQuestionsGridHolderFragment.class);
        }
    }

    private class QuestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.tag_question_card, parent, false);
            return new QVH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            QVH qvh = (QVH) holder;
            QuestionModal questionModal = questionModalList.get(position);
            qvh.setQuestionText(questionModal.getText());
        }

        @Override
        public int getItemCount() {
            return questionModalList.size();
        }
    }

    private class QVH extends RecyclerView.ViewHolder {
        private TextView textView;

        public QVH(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
        }

        public void setQuestionText(String text) {
            textView.setText(text);
        }
    }

}
