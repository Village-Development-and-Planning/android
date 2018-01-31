package com.puthuvaazhvu.mapping.other.dumpdata;

import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswerDataModal;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/25/17.
 */

@Deprecated
public class DumpData {
    private static DumpData dumpData;

    private SaveToFile saveToFile;
    private GetFromFile getFromFile;
    private AnswersInfoFile answersInfoFile;

    public static DumpData getInstance() {
        if (dumpData == null) {
            dumpData = new DumpData();
        }
        return dumpData;
    }

    private DumpData() {
        saveToFile = SaveToFile.getInstance();
        getFromFile = GetFromFile.getInstance();
        answersInfoFile = new AnswersInfoFile(getFromFile, saveToFile);
    }

    // this method also saves the data to global application data
    public Single<Optional> dumpSurvey(
            final Survey survey,
            final String snapShotID,
            final String pathToLastAnsweredQuestion,
            final boolean isSurveyIncomplete,
            final boolean isSurveyDone
    ) {

        return Single.just(survey.getAsJson().toString())
                .flatMap(new Function<String, SingleSource<? extends Optional>>() {
                    @Override
                    public SingleSource<? extends Optional> apply(@NonNull String surveyJson) throws Exception {
                        Timber.i("Survey about to be dumped: \n" + survey.getId());

                        final String fileName = survey.getId() + "_" + snapShotID;
                        File fileToSave = DataFileHelpers.getFileToDumpAnswers(fileName, false);

                        Timber.i("Survey snapshot file : \n" + fileToSave.getAbsolutePath());

                        return saveToFile.execute(surveyJson, fileToSave)
                                .flatMap(new Function<Optional, SingleSource<? extends Optional>>() {
                                    @Override
                                    public SingleSource<? extends Optional> apply(@NonNull Optional optional) throws Exception {

                                        ArrayList<AnswerDataModal.Snapshot> snapshots = new ArrayList<>();
                                        final AnswerDataModal.Snapshot snapshot = new AnswerDataModal.Snapshot(
                                                fileName,
                                                survey.getName(),
                                                pathToLastAnsweredQuestion,
                                                isSurveyIncomplete,
                                                "" + System.currentTimeMillis()
                                        );
                                        snapshots.add(snapshot);

                                        final AnswerDataModal answerDataModal =
                                                new AnswerDataModal(survey.getId(), isSurveyDone, snapshots);

                                        return answersInfoFile.updateSurvey(answerDataModal)
                                                .map(new Function<Optional, Optional>() {
                                                    @Override
                                                    public Optional apply(@NonNull Optional optional) throws Exception {

                                                        // save the application data
                                                        MappingApplication.globalContext
                                                                .getApplicationData()
                                                                .setSurvey(
                                                                        survey,
                                                                        snapshot.getSnapshotId(),
                                                                        snapshot.getPathToLastQuestion());

                                                        return new Optional(null);
                                                    }
                                                });

                                    }
                                });
                    }
                });
    }
}
