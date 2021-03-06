package org.ptracking.vdp.repository;

import android.content.Context;

import org.ptracking.vdp.filestorage.io.AnswerIO;
import org.ptracking.vdp.filestorage.io.DataInfoIO;
import org.ptracking.vdp.filestorage.modals.AnswerInfo;
import org.ptracking.vdp.filestorage.modals.DataInfo;
import org.ptracking.vdp.filestorage.modals.SurveyorData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 06/06/18.
 */

public class AnswersRepository extends Repository<List<String>> {
    private final AnswerIO answerIO;
    private final DataInfoIO dataInfoIO;

    public AnswersRepository(Context context, String surveyorCode, String password) {
        super(context, surveyorCode, password);
        answerIO = new AnswerIO();
        dataInfoIO = new DataInfoIO();
    }

    public Observable<List<File>> getAnswerFiles() {
        return dataInfoIO.read()
                .map(new Function<DataInfo, List<File>>() {
                    @Override
                    public List<File> apply(DataInfo dataInfo) throws Exception {
                        List<File> files = new ArrayList<>();

                        SurveyorData surveyorData = dataInfo.getSurveyorData(getSurveyorCode());
                        if (surveyorData != null) {
                            AnswerInfo answerInfo = surveyorData.getAnswersInfo();
                            ArrayList<AnswerInfo.Answer> answers = answerInfo.getAnswers();

                            for (AnswerInfo.Answer answer : answers) {
                                files.add(answerIO.getFile(answer.getAnswerID()));
                            }
                        }

                        return files;
                    }
                });
    }

    @Override
    public Observable<List<String>> get(boolean forceOffline) {
        throw new RuntimeException("Not implemented");
    }
}
