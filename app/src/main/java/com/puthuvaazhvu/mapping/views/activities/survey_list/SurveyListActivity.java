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
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.network.APIs;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.PrefsStorage;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;
import com.puthuvaazhvu.mapping.views.activities.main.MainActivity;
import com.puthuvaazhvu.mapping.views.activities.save_survey_data.*;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListActivity extends MenuActivity
        implements View.OnClickListener, Contract.View {

    private ProgressDialog progressDialog;

    private TextView infoTxt;

    private Contract.UserAction presenter;

    private ListAdapter adapter;

    private List<SurveyListData> surveyListData;

    private PrefsStorage prefsStorage;

    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
        prefsStorage = PrefsStorage.getInstance(sharedPreferences);

        surveyListData = new ArrayList<>();

        progressDialog = new ProgressDialog();

        setContentView(R.layout.select_survey_activity);

        infoTxt = findViewById(R.id.info_txt);

        findViewById(R.id.dump_from_server).setOnClickListener(this);
        findViewById(R.id.proceed_btn).setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);

        SurveyInfoFile surveyInfoFile = new SurveyInfoFile(GetFromFile.getInstance(), SaveToFile.getInstance());
        AnswersInfoFile answersInfoFile = new AnswersInfoFile(GetFromFile.getInstance(), SaveToFile.getInstance());
        GetFromFile getFromFile = GetFromFile.getInstance();
        SingleSurveyAPI singleSurveyAPI = SingleSurveyAPI.getInstance(APIs.getAuth(sharedPreferences));
        String optionsJson = Utils.readFromAssetsFile(this, "options_fill.json");
        SurveyDataRepository surveyDataRepository = SurveyDataRepository.getInstance(getFromFile
                , sharedPreferences, singleSurveyAPI, optionsJson);

        presenter = new SurveyListPresenter(surveyInfoFile, answersInfoFile, surveyDataRepository, this);

        //fetchListOfSurveys();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchListOfSurveys();
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

                    if (surveyListData.getStatus() == SurveyListData.STATUS.ONGOING) {
                        showSurveyOngoingDialog(surveyListData.getSurveySnapshot(), surveyListData.getName());
                    } else if (surveyListData.getStatus() == SurveyListData.STATUS.COMPLETED) {
                        showSurveyDoneDialog(surveyListData.getSurveySnapshot(), surveyListData.getId());
                    } else {
                        File file = DataFileHelpers.getSurveyFromSurveyDir(surveyListData.getId());
                        presenter.getSurveyFromFile(file, null);
                    }

//                    // set the survey as the current survey
//                    String surveyID = surveyListData.getId();
//                    Timber.i("Selected survey : " + surveyID);
//
//                    prefsStorage.saveLatestSurveyID(surveyID);
//
//                    openMainSurveyActivity();

                }
                break;
        }
    }

    public void showSurveyDoneDialog(final SurveyListData.SurveySnapShot snapshot, final String surveyID) {
        AlertDialog alertDialog = Utils.createAlertDialog(
                this,
                getString(R.string.survey_override_dialog_message),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = DataFileHelpers.getSurveyFromSurveyDir(surveyID);
                        presenter.getSurveyFromFile(file, null);
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

    public void showSurveyOngoingDialog(final SurveyListData.SurveySnapShot snapshot, final String surveyName) {
        Utils.createAlertDialog(
                this,
                String.format(getString(R.string.survey_ongoing_dialog_message), surveyName),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = DataFileHelpers.getSurveyFromAnswersDir(snapshot.getSnapshotID());
                        presenter.getSurveyFromFile(file, snapshot);
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

    private void openMainSurveyActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                fetchListOfSurveys();
            }
        }
    }

    private void fetchListOfSurveys() {
        presenter.fetchListOfSurveys();
    }

    private void startDumpSurveyActivity() {
        Intent i = new Intent(this, SurveyDataDumpActivity.class);
        startActivityForResult(i, 1);
    }

    @Override
    public void onSurveyLoaded(Survey survey, SurveyListData.SurveySnapShot snapshot) {
        Timber.i("Survey loaded : " + survey.getId());

        if (snapshot != null)
            MappingApplication.globalContext.getApplicationData()
                    .setSurvey(survey, snapshot.getSnapshotID(), snapshot.getPath());
        else
            MappingApplication.globalContext.getApplicationData().setSurvey(survey, null, null);

        openMainSurveyActivity();
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
    public void onSurveysFetched(List<SurveyListData> data) {
        if (resumed) {

            if (data.isEmpty()) {
                infoTxt.setText("No survey(s) found. Dump survey from the server first.");
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
            holder.populateViews(data.getId(), data.getName(), data.isChecked());

            if (data.getStatus() == SurveyListData.STATUS.ONGOING) {
                holder.setRowBackgroundColor(R.color.orange_light);
            } else if (data.getStatus() == SurveyListData.STATUS.COMPLETED) {
                holder.setRowBackgroundColor(R.color.green_light);
            } else {
                holder.setRowBackgroundColor(R.color.white);
            }

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

        public ViewHolder(View itemView) {
            super(itemView);

            radio_button = itemView.findViewById(R.id.radio_button);
            textView = itemView.findViewById(R.id.id_txt);
            name_txt = itemView.findViewById(R.id.name_txt);
            content_holder = itemView.findViewById(R.id.content_holder);
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

        public void populateViews(String id, String name, boolean isChecked) {
            textView.setText(id);
            name_txt.setText(name);
            radio_button.setChecked(isChecked);
        }
    }
}
