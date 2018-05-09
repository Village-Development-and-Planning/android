package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DialogHandler;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;
import com.puthuvaazhvu.mapping.views.activities.connector.ConnectorActivity;
import com.puthuvaazhvu.mapping.views.activities.main.MainActivity;
import com.puthuvaazhvu.mapping.views.activities.dump_survey_activity.*;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListActivity extends MenuActivity
        implements View.OnClickListener, Contract.View {

    private ProgressDialog progressDialog;

    DialogHandler dialogHandler;

    private TextView infoTxt;

    private Contract.UserAction presenter;

    private ListAdapter adapter;

    private List<SurveyListData> surveyListData;

    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);

        surveyListData = new ArrayList<>();

        progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);

        dialogHandler = new DialogHandler(progressDialog, getSupportFragmentManager());

        setContentView(R.layout.select_survey_activity);

        infoTxt = findViewById(R.id.info_txt);

        findViewById(R.id.dump_from_server).setOnClickListener(this);
        findViewById(R.id.proceed_btn).setOnClickListener(this);
        findViewById(R.id.connector_btn).setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);


        presenter = new SurveyListPresenter(this, sharedPreferences);
    }

    @Override
    public PauseHandler getPauseHandler() {
        return dialogHandler;
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchListOfSurveys();

        resumed = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dump_from_server:
                if (Utils.isNetworkAvailable(this))
                    startDumpSurveyActivity();
                else
                    Utils.showMessageToast(R.string.no_network, this);
                break;
            case R.id.proceed_btn:
                SurveyListData surveyListData = adapter.getSelectedData();
                if (surveyListData == null) {
                    Utils.showMessageToast("Select a valid survey", this);
                } else {
                    showAlertDialog();
                }
                break;
            case R.id.connector_btn:
                openConnectorActivity();
                break;
        }
    }

    public void showAlertDialog() {
        AlertDialog alertDialog = Utils.createAlertDialog(
                this,
                adapter.getSelectedData().isOngoing() ?
                        getString(R.string.survey_continue_msg) :
                        getString(R.string.survey_start_again_msg),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.getSurveyData(adapter.getSelectedData());
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        alertDialog.show();
    }

    private void openConnectorActivity() {
        Intent intent = new Intent(this, ConnectorActivity.class);
        startActivity(intent);
    }

    private void openMainSurveyActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //fetchSurveys();
            }
        }
    }

    private void fetchListOfSurveys() {
        presenter.fetchSurveys();
    }

    private void startDumpSurveyActivity() {
        Intent i = new Intent(this, SurveyDataDumpActivity.class);
        startActivityForResult(i, 1);
    }

    @Override
    public void onSurveyLoaded(Survey survey) {
        Timber.i("Survey loaded : " + survey.getId());

        MappingApplication.globalContext.getApplicationData()
                .setSurvey(survey);

        MappingApplication.globalContext.getApplicationData()
                .setSurveySnapShotPath(adapter.getSelectedData() != null
                        ? adapter.getSelectedData().getSnapshotPath() : null);

        openMainSurveyActivity();
    }

    @Override
    public void showSnapshotDeleteDialog(final SurveyListData surveyListData) {
        Utils.createAlertDialog(
                this,
                String.format(getString(R.string.delete_snapshot), surveyListData.getName()),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.deleteSnapshot(surveyListData);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }

    @Override
    public void onError(int msgID) {
        Utils.showMessageToast(msgID, this);
    }

    @Override
    public void showLoading(int msgID) {
        progressDialog.setTextView(getString(msgID));
        dialogHandler.showDialog("progress_dialog");
    }

    @Override
    public void hideLoading() {
        dialogHandler.hideDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        resumed = false;
    }

    @Override
    public void onSurveysFetched(List<SurveyListData> data) {
        if (resumed) {

            if (data.isEmpty()) {
                infoTxt.setText("No survey(s) found. Download surveys from the settings page.");
                return;
            }

            infoTxt.setText("Survey(s) count " + data.size());

            surveyListData.clear();
            surveyListData.addAll(data);
            adapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private boolean onBind;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_list_radio_btn, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            SurveyListData data = surveyListData.get(position);

            holder.setCheckBoxClickListener(null);

            holder.setSelected(data.isChecked());

            holder.setCheckBoxClickListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!onBind) {
                        for (SurveyListData s : surveyListData) {
                            s.setChecked(false);
                        }
                        surveyListData.get(holder.getAdapterPosition()).setChecked(isChecked);
                        ListAdapter.this.notifyDataSetChanged();
                    }
                }
            });

            onBind = true;
            // do all the view updating here
            holder.populateViews(
                    data.getId(),
                    data.getName(),
                    data.isChecked(),
                    !data.isOngoing(),
                    data.isOngoing() ? R.color.orange_light : R.color.white,
                    data.getCount()
            );

            onBind = false;
        }

        @Override
        public int getItemCount() {
            return surveyListData.size();
        }

        public SurveyListData getSelectedData() {
            for (SurveyListData d : surveyListData) {
                if (d.isChecked()) {
                    return d;
                }
            }
            return null;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final RadioButton radio_button;
        private final TextView textView;
        private final TextView name_txt;
        private final View content_holder;
        private TextView id_txt;

        public ViewHolder(View itemView) {
            super(itemView);

            radio_button = itemView.findViewById(R.id.radio_button);
            textView = itemView.findViewById(R.id.id_txt);
            name_txt = itemView.findViewById(R.id.name_txt);
            content_holder = itemView.findViewById(R.id.content_holder);
            id_txt = itemView.findViewById(R.id.badge_txt);
        }

        public void setRowBackgroundColor(int color) {
            content_holder.setBackgroundColor(getResources().getColor(color));
        }

        public void setCheckBoxClickListener(CompoundButton.OnCheckedChangeListener checkBoxClickListener) {
            radio_button.setOnCheckedChangeListener(checkBoxClickListener);
        }

        public void setSelected(boolean isChecked) {
            radio_button.setChecked(isChecked);
        }

        public void populateViews(String id,
                                  String name,
                                  boolean isChecked,
                                  boolean shouldShowCount,
                                  int color,
                                  int count) {
            textView.setText(id);
            name_txt.setText(name);
            radio_button.setChecked(isChecked);
            id_txt.setText("" + count);
            setRowBackgroundColor(color);

            if (!shouldShowCount) id_txt.setVisibility(View.INVISIBLE);
        }
    }

    private void throwTestException() {
        throw new RuntimeException("This is a test.");
    }
}
