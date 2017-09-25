package com.puthuvaazhvu.mapping.Survey;

import android.os.AsyncTask;
import android.os.Environment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.Survey.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Parsers.SurveyParser;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionType;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;
import com.puthuvaazhvu.mapping.utils.FileIO.SaveOperationCallback;
import com.puthuvaazhvu.mapping.utils.FileIO.SaveSurveyToFile;
import com.puthuvaazhvu.mapping.utils.ModalAdapters;
import com.puthuvaazhvu.mapping.utils.StorageHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.puthuvaazhvu.mapping.Constants.ErrorCodes.FILE_STORAGE_NULL;
import static com.puthuvaazhvu.mapping.Constants.ErrorCodes.PARSING_ERROR;
import static com.puthuvaazhvu.mapping.Constants.ErrorCodes.SAVING_ERROR;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class SurveyActivityPresenter {
    private SurveyActivityCommunicationInterface communicationInterface;
    private Parser parser;
    private ResponsesToJson responsesToJson;
    private Survey survey;
    private HashMap<String, Object> completedSurveyQuestions = new HashMap<>();

    public SurveyActivityPresenter(SurveyActivityCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    public File getSaveJsonFile(String dirName, String fileName) {
        if (StorageHelpers.isExternalStorageWritable()) {
            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + "/" + dirName);
            boolean dirExists = dir.exists();
            if (!dirExists)
                dirExists = dir.mkdirs();
            if (dirExists) {
                return new File(dir, fileName);
            }
        }
        return null;
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
            switch (questionModal.getQuestionTypes()) {
                case LOOP:
                    communicationInterface.loadLoopQuestionFragment(questionModal);
                    break;
                default:
                    throw new RuntimeException("The question type "
                            + questionModal.getQuestionTypes().name() + " is not handled in UI");
            }
        } else {
            // all questions completed
            saveResponses();
        }
    }

    private void saveResponses() {
        if (responsesToJson != null) {
            responsesToJson.cancel(true);
        }

        if (survey == null) {
            throw new RuntimeException("The survey object is null.");
        }

        responsesToJson = new ResponsesToJson(survey.getId()
                , survey.getName()
                , completedSurveyQuestions
                , new ResponsesToJson.ResponseToJsonCallbacks() {
            @Override
            public void onResponsesConvertedToJson(String error, JsonObject result) {
                if (result != null) {
                    // TODO: surveyor id.
                    String resultantFileName = StorageHelpers.getSurveyResponsesFileName(survey.getId(), null, null);
                    File file = getSaveJsonFile(Constants.DataStorage.APP_DIR_SURVEY, resultantFileName);
                    if (file == null) {
                        communicationInterface.onError(FILE_STORAGE_NULL);
                    } else {
                        saveJsonToFile(result, file);
                    }
                } else {
                    communicationInterface.onError(PARSING_ERROR);
                }
            }
        });
        responsesToJson.execute();
    }

    public void saveJsonToFile(JsonObject result, File file) {
        SaveSurveyToFile saveSurveyToFile = new SaveSurveyToFile(result, file, new SaveOperationCallback() {
            @Override
            public void done() {
                // clear the survey once all the answers are saved.
                completedSurveyQuestions.clear();
                communicationInterface.onSurveyDone();
            }

            @Override
            public void error(String message) {
                communicationInterface.onError(SAVING_ERROR);
            }
        });
        saveSurveyToFile.save();
    }

    public void logAnsweredQuestions(HashMap<String, HashMap<String, QuestionModal>> result) {
        HashMap.Entry<String, HashMap<String, QuestionModal>> entry = result.entrySet().iterator().next();
        completedSurveyQuestions.put(entry.getKey(), entry.getValue());
    }

    public void logAnsweredQuestions(String questionID, QuestionModal questionModal) {
        completedSurveyQuestions.put(questionID, questionModal);
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
            if (result.getQuestionTypes() != QuestionType.LOOP) {
                result = null;
                continue;
            }
            break;
        }
        return result;
    }

    private boolean checkIfSurveyQuestionIsDone(Question question) {
        return completedSurveyQuestions.containsKey(question.getId());
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
                throw new RuntimeException("The json data passed to Parse async task is null. ");
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

    /**
     * Helper to convert the survey responses into a single json asynchronously.
     */
    private static class ResponsesToJson extends AsyncTask<Void, Void, JsonObject> {
        HashMap<String, Object> completedSurveyQuestions;

        interface ResponseToJsonCallbacks {
            void onResponsesConvertedToJson(String error, JsonObject result);
        }

        ResponseToJsonCallbacks callbacks;
        String surveyName,
                surveyID;

        public ResponsesToJson(String surveyID,
                               String surveyName,
                               HashMap<String, Object> completedSurveyQuestions,
                               ResponseToJsonCallbacks callbacks) {
            this.completedSurveyQuestions = completedSurveyQuestions;
            this.callbacks = callbacks;
            this.surveyID = surveyID;
            this.surveyName = surveyName;
        }

        @Override
        protected JsonObject doInBackground(Void... voids) {
            JsonObject result = new JsonObject();
            result.addProperty("survey_id", surveyID);
            result.addProperty("survey_name", surveyName);

            JsonArray answersJsonArray = new JsonArray();
            result.add("answers", answersJsonArray);
            for (String key : completedSurveyQuestions.keySet()) {
                JsonObject r = new JsonObject();
                r.addProperty("question_id", key);
                Object response = completedSurveyQuestions.get(key);
                if (response instanceof HashMap) {
                    JsonArray aja = new JsonArray();
                    // This is of the form <option_id, question_modal>
                    HashMap<String, QuestionModal> rhm = (HashMap<String, QuestionModal>) response;
                    for (String optionKey : rhm.keySet()) {
                        QuestionModal qm = rhm.get(optionKey);
                        JsonObject qmJo = toJson(qm);
                        JsonObject qmJoo = new JsonObject();
                        qmJoo.addProperty("option_id", optionKey);
                        qmJoo.add("response", qmJo);
                        aja.add(qmJoo);
                    }
                    r.add("option_data", aja);
                }
                // do for other response types.

                answersJsonArray.add(r);
            }
            return result;
        }

        private JsonObject toJson(QuestionModal questionModal) {
            JsonObject result = new JsonObject();
            JsonArray cja = new JsonArray();
            JsonArray oja = new JsonArray();
            for (OptionData od : questionModal.getOptionDataList()) {
                JsonObject oj = new JsonObject();
                oj.addProperty("option_id", od.getId());
                JsonObject odj = new JsonObject();
                odj.addProperty("is_selected", od.isChecked());
                oj.add("response", odj);
                oja.add(oj);
            }
            result.addProperty("question_id", questionModal.getQuestionID());
            result.addProperty("iterator_id", questionModal.getIterationID());
            result.add("option_data", oja);
            result.add("children", cja);

            for (QuestionModal qm : questionModal.getChildren()) {
                cja.add(toJson(qm));
            }
            return result;
        }

        @Override
        protected void onPostExecute(JsonObject jsonObject) {
            if (jsonObject != null) {
                callbacks.onResponsesConvertedToJson(null, jsonObject);
            } else {
                callbacks.onResponsesConvertedToJson("Error converting the responses to JSON", null);
            }
        }
    }
}
