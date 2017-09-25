package com.puthuvaazhvu.mapping.Survey.Fragments.DynamicTypes;

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

import com.puthuvaazhvu.mapping.Survey.Modals.GridQuestionModal;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/21/17.
 */

public class GridQuestionFragment extends BaseDynamicTypeFragment {
    private RecyclerView recyclerView;
    private QuestionsAdapter questionsAdapter;
    private ArrayList<GridQuestionModal> dataSet;

    public static GridQuestionFragment getInstance(ArrayList<GridQuestionModal> questionModal) {
        GridQuestionFragment questionFragment = new GridQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("question_data", questionModal);

        questionFragment.setArguments(bundle);

        return questionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSet = getArguments().getParcelableArrayList("question_data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.questions_grid_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
                GridQuestionModal gridQuestionModal = dataSet.get(position);
                getDynamicFragmentTypeCommunicationInterface().OnShowNextFragment(gridQuestionModal);
            }
        }));

        questionsAdapter = new QuestionsAdapter();
        recyclerView.setAdapter(questionsAdapter);
    }

    @Override
    public void onClick(View view) {

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
            GridQuestionModal gridQuestionModal = dataSet.get(position);
            qvh.populateViews(gridQuestionModal.getText()
                    , gridQuestionModal.getQuestionCount());
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
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