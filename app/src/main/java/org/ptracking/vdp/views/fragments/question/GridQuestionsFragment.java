package org.ptracking.vdp.views.fragments.question;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.utils.RecyclerItemClickListener;
import org.ptracking.vdp.views.fragments.question.Communicationinterfaces.GridQuestionFragmentCallbacks;
import org.ptracking.vdp.views.fragments.question.types.QuestionFragmentTypes;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class GridQuestionsFragment extends QuestionFragment {
    private RecyclerView recyclerView;
    private QuestionsAdapter questionsAdapter;

    private GridQuestionFragmentCallbacks gridQuestionFragmentCallbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        gridQuestionFragmentCallbacks = (GridQuestionFragmentCallbacks) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.questions_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                gridQuestionFragmentCallbacks.onGridItemClicked(position);
            }
        }));

        Answer currentAnswer = currentQuestion.getCurrentAnswer();
        if (currentAnswer != null && !currentAnswer.getChildren().isEmpty()) {
            final ArrayList<Question> children = currentAnswer.getChildren();

            questionsAdapter = new QuestionsAdapter(children);
            recyclerView.setAdapter(questionsAdapter);
        } else {
            Timber.e("The current answer is null or the children are empty for the question "
                    + currentQuestion.getNumber());
        }
    }

    @Override
    public void onBackButtonPressed(View view) {
        callbacks.onBackPressed(QuestionFragmentTypes.GRID);
    }

    @Override
    public void onNextButtonPressed(View view) {
        callbacks.onNextPressed(QuestionFragmentTypes.GRID, null);
    }

    private class QuestionsAdapter extends RecyclerView.Adapter<QVH> {
        private ArrayList<Question> children;

        public QuestionsAdapter(ArrayList<Question> children) {
            this.children = children;
        }

        @Override
        public QVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.tag_question_card, parent, false);
            return new QVH(view);
        }

        @Override
        public void onBindViewHolder(QVH holder, int position) {
            Question child = children.get(position);

            int count = child.getBubbleAnswersCount();
            holder.populateViews(child.getTextString(), count);
        }

        @Override
        public int getItemCount() {
            return children.size();
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
