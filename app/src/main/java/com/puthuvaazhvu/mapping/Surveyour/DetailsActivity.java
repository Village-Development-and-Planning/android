package com.puthuvaazhvu.mapping.Surveyour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.Survey.SurveyActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/31/17.
 */

public class DetailsActivity extends AppCompatActivity
        implements View.OnClickListener, DetailsActivityCommunicationInterface {
    RecyclerView recyclerView;
    ArrayList<SurveyorDetailsModal> surveyorDetailsModals = new ArrayList<>();
    DetailsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surveyor_details_page);

        findViewById(R.id.next).setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DetailsAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.next) {
            Intent intent = new Intent(DetailsActivity.this, SurveyActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onSurveyorDetailsFetched(ArrayList<SurveyorDetailsModal> surveyorDetailsModals) {
        if (recyclerView != null && adapter != null) {
            this.surveyorDetailsModals.clear();
            this.surveyorDetailsModals.addAll(surveyorDetailsModals);
            adapter.notifyDataSetChanged();
        }
    }

    public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DVH(LayoutInflater.from(DetailsActivity.this).inflate(R.layout.surveyor_details_row, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DVH dvh = (DVH) holder;
            SurveyorDetailsModal surveyorDetailsModal = surveyorDetailsModals.get(position);
            dvh.populateViews(surveyorDetailsModal.holder, surveyorDetailsModal.title);
        }

        @Override
        public int getItemCount() {
            return surveyorDetailsModals.size();
        }
    }

    public class DVH extends RecyclerView.ViewHolder {
        TextView left_txt;
        TextView right_txt;

        public DVH(View itemView) {
            super(itemView);

            left_txt = itemView.findViewById(R.id.left_txt);
            right_txt = itemView.findViewById(R.id.right_txt);
        }

        public void populateViews(String holderTxt, String title) {
            left_txt.setText(holderTxt);
            right_txt.setText(title);
        }
    }
}
