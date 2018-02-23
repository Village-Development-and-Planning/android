package com.puthuvaazhvu.mapping.utils.logger;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 23/02/18.
 */

public class SurveyObjectLogger implements ObjectLogger {
    private final Survey survey;

    public SurveyObjectLogger(Survey survey) {
        this.survey = survey;
    }

    @Override
    public void log() {
        log("Survey ID: " + survey.getId() + "\n");
        logQuestions(survey.getQuestion());
    }

    private void logQuestions(Question question) {
        log("Question Number: " + question.getNumber() + " Hashcode " + question.hashCode() + "\n");
        for (Question c : question.getChildren()) {
            logQuestions(c);
        }
        for (Answer a : question.getAnswers()) {
            log("Answer Reference Question Number " + a.getParentQuestion().getNumber() + "\n");
            log("Answer Hashcode " + a.hashCode() + "\n");
            for (Question c : a.getChildren()) {
                logQuestions(c);
            }
        }
    }

    private void log(String msg) {
        Timber.i(msg);
    }
}
