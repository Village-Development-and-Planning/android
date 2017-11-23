package com.puthuvaazhvu.mapping.views.activities.survey_list;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswerDataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.DataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SurveyInfoFileDataModal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListPresenter implements Contract.UserAction {
    private final SurveyInfoFile surveyInfoFile;
    private final AnswersInfoFile answersInfoFile;
    private final Contract.View callback;
    private final SurveyDataRepository dataRepository;

    public SurveyListPresenter(
            SurveyInfoFile surveyInfoFile,
            AnswersInfoFile answersInfoFile,
            SurveyDataRepository dataRepository,
            Contract.View callback
    ) {
        this.surveyInfoFile = surveyInfoFile;
        this.answersInfoFile = answersInfoFile;
        this.callback = callback;
        this.dataRepository = dataRepository;
    }

    @Override
    public void getSurveyFromFile(final File file) {
        callback.showLoading(R.string.loading);

        Single<Survey> surveySingle;

        if (file.getAbsolutePath().contains(Constants.ANSWERS_DATA_DIR)) {
            surveySingle = dataRepository.getSurveyFromFileAndUpdateWithAnswers(file);
        } else {
            surveySingle = dataRepository.getSurveyFromFile(file);
        }

        surveySingle
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Survey>() {
                    @Override
                    public void accept(@NonNull Survey survey) throws Exception {
                        callback.hideLoading();
                        callback.onSurveyLoaded(survey);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        callback.hideLoading();
                        callback.onError(R.string.err_no_data);
                    }
                });
    }

    @Override
    public void fetchListOfSurveys() {

        Single.zip(
                surveyInfoFile.getInfoJsonParsed(),
                answersInfoFile.getInfoJsonParsed(),
                new BiFunction<SurveyInfoFileDataModal, AnswersInfoFileDataModal, ArrayList<SurveyListData>>() {
                    @Override
                    public ArrayList<SurveyListData> apply(
                            @NonNull SurveyInfoFileDataModal surveyInfoFileDataModal,
                            @NonNull AnswersInfoFileDataModal answersInfoFileDataModal) throws Exception {

                        ArrayList<SurveyListData> resultArrayList = new ArrayList<>();
                        List<DataModal> surveyInfoFileDataList = surveyInfoFileDataModal.getSurveyData();

                        for (DataModal dm : surveyInfoFileDataList) {

                            AnswerDataModal answersDataModal = answersInfoFileDataModal.find(dm.getId());
                            SurveyListData surveyListData;

                            if (answersDataModal != null && answersDataModal.getLatestSnapShot() != null) {

                                surveyListData = new SurveyListData(
                                        dm.getId(),
                                        dm.getSurveyName(),
                                        false,
                                        answersDataModal.getLatestSnapShot(),
                                        answersDataModal.isDone() ? SurveyListData.STATUS.COMPLETED
                                                : SurveyListData.STATUS.ONGOING
                                );

                            } else {

                                surveyListData = new SurveyListData(
                                        dm.getId(),
                                        dm.getSurveyName(),
                                        false
                                );
                            }

                            resultArrayList.add(surveyListData);
                        }

                        return resultArrayList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ArrayList<SurveyListData>>() {
                    @Override
                    public void accept(@NonNull ArrayList<SurveyListData> surveyListDataList) throws Exception {
                        callback.onSurveysFetched(surveyListDataList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        callback.onError(R.string.err_no_data);
                    }
                });
    }
}
