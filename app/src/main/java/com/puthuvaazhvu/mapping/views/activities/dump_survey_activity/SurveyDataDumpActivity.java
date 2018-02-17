package com.puthuvaazhvu.mapping.views.activities.dump_survey_activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyDataDumpActivity extends MenuActivity
        implements View.OnClickListener, Contract.View {

    private ListSurveyAdapter listSurveyAdapter;
    private List<SurveyInfoData> data;

    private ProgressDialog progressDialog;

    private Contract.UserAction presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);

        presenter = new Presenter(sharedPreferences, this);

        setContentView(R.layout.save_survey_data);

        findViewById(R.id.save_survey_btn).setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        data = new ArrayList<>();

        listSurveyAdapter = new ListSurveyAdapter(data);
        recyclerView.setAdapter(listSurveyAdapter);

        presenter.fetchListOfSurveys();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save_survey_btn) {

            List<SurveyInfoData> surveyInfoArrayList = getSelectedSurveyData();

            if (surveyInfoArrayList.isEmpty()) {
                Utils.showMessageToast("Please select a survey", v.getContext());
            } else {

                // proceed with saving all the surveys
                presenter.save(surveyInfoArrayList);
            }
        }
    }

    private List<SurveyInfoData> getSelectedSurveyData() {
        List<SurveyInfoData> surveyInfoArrayList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            SurveyInfoData surveyInfoData = data.get(i);
            if (surveyInfoData.isSelected) {
                surveyInfoArrayList.add(surveyInfoData);
            }
        }
        return surveyInfoArrayList;
    }

    @Override
    public void onError(int msgID) {
        Utils.showMessageToast(msgID, this);
    }

    @Override
    public void showLoading(int msgID) {
        if (progressDialog.isVisible() || progressDialog.isAdded()) {
            progressDialog.dismiss();
        }
        progressDialog.setTextView(getString(msgID));
        progressDialog.show(getSupportFragmentManager(), "progress_dialog");
    }

    @Override
    public void hideLoading() {
        if (progressDialog.isVisible())
            progressDialog.dismiss();
    }

    @Override
    public void onSurveyInfoFetched(List<SurveyInfoData> surveyInfoList) {
        if (resumed) {
            data.clear();
            data.addAll(surveyInfoList);
            listSurveyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finishActivity() {
        if (resumed) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    private class ListSurveyAdapter extends RecyclerView.Adapter<ListSurveyAdapterViewHolder> {
        private final List<SurveyInfoData> surveyInfoList;

        public ListSurveyAdapter(List<SurveyInfoData> surveyInfoList) {
            this.surveyInfoList = surveyInfoList;
        }

        @Override
        public ListSurveyAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListSurveyAdapterViewHolder listSurveyAdapterViewHolder
                    = new ListSurveyAdapterViewHolder(
                    LayoutInflater.from(SurveyDataDumpActivity.this).inflate(R.layout.survey_list_row_checkbox, parent, false)
            );
            return listSurveyAdapterViewHolder;
        }

        @Override
        public void onBindViewHolder(final ListSurveyAdapterViewHolder holder, int position) {

            SurveyInfoData surveyInfoData = surveyInfoList.get(position);

            holder.setCheckBoxClickListener(null);

            holder.setChecked(surveyInfoData.isSelected);

            holder.setCheckBoxClickListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    surveyInfoList.get(holder.getAdapterPosition()).isSelected = isChecked;
                }
            });

            holder.populateViews(surveyInfoData.name, surveyInfoData.id);
        }

        @Override
        public int getItemCount() {
            return surveyInfoList.size();
        }
    }

    private static class ListSurveyAdapterViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView titleTxt, idTxt;

        public ListSurveyAdapterViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            titleTxt = itemView.findViewById(R.id.name_txt);
            idTxt = itemView.findViewById(R.id.id_txt);
        }

        public void setCheckBoxClickListener(CompoundButton.OnCheckedChangeListener checkBoxClickListener) {
            checkBox.setOnCheckedChangeListener(checkBoxClickListener);
        }

        public void setChecked(boolean isChecked) {
            checkBox.setChecked(isChecked);
        }

        public void populateViews(String title, String id) {
            titleTxt.setText(title);
            idTxt.setText(id);
        }
    }
}
