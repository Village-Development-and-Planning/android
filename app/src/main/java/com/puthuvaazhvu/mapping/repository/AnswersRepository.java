package com.puthuvaazhvu.mapping.repository;

import android.content.Context;

import com.puthuvaazhvu.mapping.filestorage.io.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.io.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.modals.AnswerInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveyorInfo;

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

                        SurveyorInfo surveyorInfo = dataInfo.getSurveyorInfo(getSurveyorCode());
                        if (surveyorInfo != null) {
                            AnswerInfo answerInfo = surveyorInfo.getAnswersInfo();
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
