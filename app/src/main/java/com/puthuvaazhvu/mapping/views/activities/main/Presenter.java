package com.puthuvaazhvu.mapping.views.activities.main;

import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.modals.flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileData;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.IFlow;
import com.puthuvaazhvu.mapping.views.helpers.ResponseData;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.IBackFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final Contract.View activityView;
    private final DataRepository<Survey> dataRepository;

    private FlowHelper flowHelper;

    private Survey survey;

    private final Handler uiHandler;

    private final SaveToFile saveToFile;
    private final GetFromFile getFromFile;

    private final AnswersInfoFile answersInfoFile;

    public Presenter(Contract.View activityView, DataRepository<Survey> dataRepository, Handler uiHandler, SaveToFile saveToFile, GetFromFile getFromFile) {
        this.activityView = activityView;
        this.dataRepository = dataRepository;
        this.uiHandler = uiHandler;
        this.saveToFile = saveToFile;
        this.getFromFile = getFromFile;

        this.answersInfoFile = new AnswersInfoFile(getFromFile, saveToFile);
    }

    @Override
    public void loadSurvey(final String surveyID) {

        activityView.showLoading(R.string.loading);

        File file = DataFileHelpers.getSurveyDataFile(surveyID, true);

        if (file != null && file.exists()) {
            dataRepository.getData(file.getAbsolutePath()
                    , new DataRepository.DataLoadedCallback<Survey>() {
                        @Override
                        public void onDataLoaded(Survey data) {
                            activityView.hideLoading();
                            activityView.onSurveyLoaded(data);
                        }

                        @Override
                        public void onError(String msg) {
                            Timber.e("Error loading survey file " + surveyID + " msg: " + msg);
                            activityView.hideLoading();
                            activityView.onError(R.string.cannot_get_data);
                        }
                    });
        } else {
            activityView.onError(R.string.file_not_exist);
        }
    }

    @Override
    public void initData(Survey survey, FlowHelper flowHelper) {
        // init
        setSurveyQuestionFlow(flowHelper);
        this.survey = survey;
    }

    @Override
    public void finishCurrent(QuestionData questionData) {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        flowHelper.finishCurrentQuestion();
    }

    public void setSurveyQuestionFlow(FlowHelper flowHelper) {
        this.flowHelper = flowHelper;
    }

    @Override
    public void moveToQuestionAt(int index) {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        Question current = flowHelper.moveToIndex(index).getCurrent();

        // call UI with the set question
        showSingleQuestionUI(current);
    }

    @Override
    public void dumpSurveyToFile() {

        activityView.showLoading(R.string.survey_file_saving_msg);

        JsonObject resultSurveyJson = survey.getAsJson().getAsJsonObject();
        Timber.i("Survey dump: \n" + resultSurveyJson.toString());

        File fileToSave = DataFileHelpers.getFileToDumpAnswers(survey.getId(), false);
        if (fileToSave != null)
            dumpToFile(resultSurveyJson.toString(), fileToSave);
    }

    @Override
    public void updateCurrentQuestion(final QuestionData questionData, final Runnable runnable) {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        // this seems to be a little heavy process, so run in background thread.
        // references does'nt matter as the process is not too big to leak memory.
        new Thread(new Runnable() {
            @Override
            public void run() {
                flowHelper.update(ResponseData.adapter(questionData));
                uiHandler.post(runnable);
            }
        }).start();
    }

    @Override
    public void getNext() {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        // 1. get the next question to be shown
        IFlow.FlowData flowData = flowHelper.getNext();

        // 2. remove the answered questions from the stack.
        //    This comes 2nd because the remove question are populated in the flow helper only.
        // removeQuestionsFromStack();

        // 3. show the new question
        Question question = flowData.question;

        // check question UI flow
        if (question != null) {

            QuestionFlow questionFlow = question.getFlowPattern().getQuestionFlow();

            while (questionFlow.getUiMode() == QuestionFlow.UI.NONE) {

                // create dummy answer
                ArrayList<Option> dummyOptions = new ArrayList<>();
                dummyOptions.add(
                        new Option(
                                "-1",
                                "DUMMY",
                                new Text("-1", "DUMMY", "DUMMY", null),
                                "",
                                "-1"
                        )
                );

                ResponseData responseData = new ResponseData("-1", question.getRawNumber(), dummyOptions);
                flowHelper.update(responseData);

                flowData = flowHelper.getNext();
                question = flowData.question;
                questionFlow = question.getFlowPattern().getQuestionFlow();
            }
        }

        if (question != null) {

            ChildFlow childFlow = question.getFlowPattern().getChildFlow();

            if (childFlow != null) {
                if (childFlow.getMode() == ChildFlow.Modes.TOGETHER) {
                    activityView.shouldShowTogetherQuestion(question, QuestionData.adapter(question));
                    return;
                }
            }
        }

        // only if grid for children, show grid. else show normal question view
        if (flowData.flowType == FlowType.GRID) {
            showGridUI(question);
        } else if (flowData.flowType == FlowType.SINGLE) {
            showSingleQuestionUI(question);
        } else if (flowData.flowType == FlowType.END) {
            activityView.onSurveyEnd();
        } else {
            Timber.e("Invalid UI data provided. number:  " + question.getRawNumber());
            activityView.onError(R.string.invalid_data);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    activityView.hideLoading();
                }
            });
        }
    }

    @Override
    public void getPrevious() {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        IBackFlow.BackFlowData backFlowData = flowHelper.getPrevious();

        Question previous = backFlowData.question;

        if (previous == null) {
            activityView.onError(R.string.cannot_go_back);
        } else {
            showSingleQuestionUI(previous);
        }
    }

    private void showGridUI(Question question) {
        // get the children of the latest answer
        ArrayList<Question> children = question.getCurrentAnswer().getChildren();
        ArrayList<GridQuestionData> data = GridQuestionData.adapter(children);
        activityView.shouldShowGrid(QuestionData.adapter(question), data);
    }

    private void showSingleQuestionUI(Question question) {

        if (question.getFlowPattern() == null) {
            if (question.isRoot()) {
                activityView.toggleDefaultBackPressed(true);
            }
            return;
        }

        QuestionData questionData = QuestionData.adapter(question);

        // check and show UI accordingly
        switch (question.getFlowPattern().getQuestionFlow().getUiMode()) {
            case INFO:
                activityView.shouldShowQuestionAsInfo(questionData);
                break;
            case CONFIRMATION:
                activityView.shouldShowConformationQuestion(questionData);
                break;
            case MESSAGE:
                activityView.shouldShowMessageQuestion(questionData);
                break;
            default:
                activityView.shouldShowSingleQuestion(questionData);
        }
    }

    private void removeQuestionsFromStack() {
        ArrayList<Question> toBeRemoved = flowHelper.emptyToBeRemovedList();
        if (!toBeRemoved.isEmpty())
            activityView.remove(toBeRemoved);
    }

    private void dumpToFile(String toSave, File file) {
        saveToFile.execute(toSave, file, new SaveToFile.SaveToFileCallbacks() {
            @Override
            public void onFileSaved() {
                Timber.i("The data is saved to the file successfully.");
                activityView.hideLoading();
                //activityView.shouldShowSummary(survey);
                activityView.onSurveySaved(survey);
            }

            @Override
            public void onErrorWhileSaving(String message) {
                String errorMessage = "Error while saving file: " + message;
                Timber.e(errorMessage);
                activityView.hideLoading();

                // send report to fabric.io
                Crashlytics.logException(new Exception(errorMessage));
            }
        });
    }

    @Override
    public void updateAnswersInfoFile(final Survey survey) {

        activityView.showLoading(R.string.loading);

        new Thread(new Runnable() {
            ExecutorService thread = Executors.newSingleThreadExecutor();

            @Override
            public void run() {

                try {
                    thread.submit(answersInfoFile.updateListOfSurveys(
                            AnswersInfoFileData.adapter(survey.getId(), survey.getName())))
                            .get();
                } catch (Exception e) {
                    String err = "Error updating answers " + Constants.INFO_FILE_NAME + " error msg: " + e.getMessage();
                    Timber.e(err);
                    Crashlytics.log(err);
                }

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        activityView.hideLoading();
                    }
                });
            }
        }).start();
    }

}
