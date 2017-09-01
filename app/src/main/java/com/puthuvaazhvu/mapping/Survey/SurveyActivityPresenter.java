package com.puthuvaazhvu.mapping.Survey;

import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.Parsers.SurveyParser;
import com.puthuvaazhvu.mapping.Question.QUESTION_TYPE;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.utils.DeepCopy.DeepCopy;
import com.puthuvaazhvu.mapping.utils.ModalAdapters;
import com.puthuvaazhvu.mapping.utils.ObjectToFromDisk.ObjectToFromDiskAsync;
import com.puthuvaazhvu.mapping.utils.ObjectToFromDisk.SaveToDiskAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class SurveyActivityPresenter {
    SurveyActivityCommunicationInterface communicationInterface;
    Parser parser;
    Survey survey;
    Set<String> completedSurveyQuestionIds = new HashSet<>();
    Set<String> completedSavingQuestionIds = new HashSet<>();

    public SurveyActivityPresenter(SurveyActivityCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    public void parseSurveyJson(String json) {
        cancelParsingAsyncTask();
        parser = new Parser(new Parser.ParserCallbacks() {
            @Override
            public void onParsed(Survey survey) {
                if (communicationInterface != null) {
                    if (survey != null) {
                        SurveyActivityPresenter.this.survey = survey;
                        communicationInterface.parsedSurveyData(survey);
                    } else {
                        communicationInterface.onError(Constants.ErrorCodes.NULL_DATA);
                    }
                }
            }
        });
        parser.execute(json);
    }

    public void getNextQuestionAndDirectToFragments() {
        QuestionModal questionModal = getNextSurveyQuestion();
        if (questionModal != null) {
            switch (questionModal.getQuestionType()) {
                case LOOP:
                    communicationInterface.loadLoopQuestionFragment(questionModal);
                    break;
                default:
                    throw new RuntimeException("The question type "
                            + questionModal.getQuestionType().name() + " is not handled in UI");
            }
        } else {
            communicationInterface.onSurveyDone();
        }
    }

    public void saveObjectToDisk(final HashMap<String, HashMap<String, QuestionModal>> result, String basePath) {
        ObjectToFromDiskAsync.getInstance().saveObjectToDisk(result
                , survey.getId()
                , basePath
                , new SaveToDiskAsync.SaveToDiskCallback() {
                    @Override
                    public void onDone() {
                        String questionID = result.keySet().iterator().next();
                        completedSavingQuestionIds.add(questionID);
                        boolean isCompleted = false;
                        for (Question q : survey.getQuestionList()) {
                            QuestionModal questionModal = ModalAdapters.getAsQuestionModal(q, Constants.isTamil);
                            if (questionModal.getQuestionType() == QUESTION_TYPE.DETAILS) {
                                continue;
                            }
                            isCompleted = completedSavingQuestionIds.contains(q.getId());
                            if (!isCompleted) {
                                break;
                            }
                        }

                        if (isCompleted) {
                            if (communicationInterface != null) {
                                communicationInterface.onAllQuestionsSaved();
                            }
                        }
                    }
                });
    }

    public void logAnsweredQuestions(HashMap<String, HashMap<String, QuestionModal>> result) {
        for (String id : result.keySet()) {
            logAnsweredQuestions(id);
        }
    }

    private void logAnsweredQuestions(String questionID) {
        completedSurveyQuestionIds.add(questionID);
    }

    public void cancelParsingAsyncTask() {
        if (parser != null)
            parser.cancel(true);
    }

    private QuestionModal getNextSurveyQuestion() {
        if (survey == null) {
            throw new RuntimeException("The survey object is null. ");
        }

        ArrayList<Question> surveyQuestions = survey.getQuestionList();
        QuestionModal result = null;

        for (int i = 0; i < surveyQuestions.size(); i++) {
            Question question = surveyQuestions.get(i);
            if (checkIfSurveyQuestionIsDone(question)) {
                continue;
            }
            result = ModalAdapters.getAsQuestionModal(question, Constants.isTamil);
            if (result.getQuestionType() == QUESTION_TYPE.DETAILS) {
                result = null;
                continue;
            }
            break;
        }
        return result;
    }

    private boolean checkIfSurveyQuestionIsDone(Question question) {
        return completedSurveyQuestionIds.contains(question.getId());
    }

    private static class Parser extends AsyncTask<String, Void, Survey> {
        SurveyParser surveyParser;
        ParserCallbacks parserCallbacks;

        interface ParserCallbacks {
            public void onParsed(Survey survey);
        }

        public Parser(ParserCallbacks parserCallbacks) {
            this.surveyParser = SurveyParser.getInstance();
            this.parserCallbacks = parserCallbacks;
        }

        @Override
        protected Survey doInBackground(String... strings) {
            String json = strings[0];

            if (json == null) {
                throw new RuntimeException("The json data pased to Parse async task is null. ");
            }

            JsonParser jsonParser = new JsonParser();
            JsonObject surveyJson = jsonParser.parse(json).getAsJsonObject();

            return surveyParser.parseSurvey(surveyJson);
        }

        @Override
        protected void onPostExecute(Survey survey) {
            parserCallbacks.onParsed(survey);
        }
    }
}
