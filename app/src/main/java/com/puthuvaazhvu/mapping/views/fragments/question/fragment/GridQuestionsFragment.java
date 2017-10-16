package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.RecyclerItemClickListener;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.SingleQuestion;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class GridQuestionsFragment extends QuestionFragment implements View.OnClickListener {
    private ArrayList<GridQuestionData> data;
    private QuestionData parentData;

    private RecyclerView recyclerView;
    private QuestionsAdapter questionsAdapter;

    private Button next_button;
    private Button back_button;

    public static GridQuestionsFragment getInstance(QuestionData parentQuestion, ArrayList<GridQuestionData> data) {
        GridQuestionsFragment fragment = new GridQuestionsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", data);
        bundle.putParcelable("parent_data", parentQuestion);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.questions_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        data = getArguments().getParcelableArrayList("data");
        parentData = getArguments().getParcelable("parent_data");

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
                        recyclerView.setLayoutManager(gridLayoutManager);
                    }
                });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext()
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                QuestionData questionData = data.get(position);
                questionData.setPosition(position);
                sendQuestionToCaller(questionData, true); // send the selected question only
            }
        }));

        next_button = view.findViewById(R.id.next_button);
        back_button = view.findViewById(R.id.back_button);

        next_button.setOnClickListener(this);
        back_button.setOnClickListener(this);

        questionsAdapter = new QuestionsAdapter();
        recyclerView.setAdapter(questionsAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_button) {
            finishCurrentQuestion(parentData, false);
        } else if (v.getId() == R.id.back_button) {
            backButtonPressedInsideQuestion(parentData);
        }
    }

    private class QuestionsAdapter extends RecyclerView.Adapter<QVH> {

        @Override
        public QVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.tag_question_card, parent, false);
            return new QVH(view);
        }

        @Override
        public void onBindViewHolder(QVH holder, int position) {
            GridQuestionData data = GridQuestionsFragment.this.data.get(position);
            SingleQuestion singleQuestion = data.getSingleQuestion();
            holder.populateViews(singleQuestion.getText(), data.getCount());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class QVH extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView img_check_mark;
        private TextView badge;

        public QVH(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            badge = itemView.findViewById(R.id.badge_txt);
            img_check_mark = itemView.findViewById(R.id.img_check_mark);
            img_check_mark.setVisibility(View.GONE);
        }

        public void populateViews(String text, int count) {
            setQuestionText(text);
            badge.setText(String.valueOf(count));

            if (count <= 0) {
                badge.setVisibility(View.GONE);
            } else {
                badge.setVisibility(View.VISIBLE);
            }
        }

        public void setQuestionText(String text) {
            textView.setText(text);
        }
    }
}
