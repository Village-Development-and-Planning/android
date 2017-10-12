package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

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

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.RecyclerItemClickListener;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class GridQuestionsFragment extends QuestionFragment {
    private ArrayList<GridData> datas;
    private RecyclerView recyclerView;
    private QuestionsAdapter questionsAdapter;

    public static GridQuestionsFragment getInstance(ArrayList<GridData> datas) {
        GridQuestionsFragment fragment = new GridQuestionsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", datas);
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
        datas = getArguments().getParcelableArrayList("data");

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
                Data data = datas.get(position);
                sendQuestionToCaller(data, true, false); // send the selected question only
            }
        }));

        questionsAdapter = new QuestionsAdapter();
        recyclerView.setAdapter(questionsAdapter);
    }

    private class QuestionsAdapter extends RecyclerView.Adapter<QVH> {

        @Override
        public QVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.tag_question_card, parent, false);
            return new QVH(view);
        }

        @Override
        public void onBindViewHolder(QVH holder, int position) {
            GridData data = datas.get(position);
            com.puthuvaazhvu.mapping.views.fragments.question.modals.Question question = data.getQuestion();
            holder.populateViews(question.getText(), data.getCount());
        }

        @Override
        public int getItemCount() {
            return datas.size();
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
