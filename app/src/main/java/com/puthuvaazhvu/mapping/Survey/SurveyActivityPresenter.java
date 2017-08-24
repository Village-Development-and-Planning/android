package com.puthuvaazhvu.mapping.Survey;

import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.Parsers.SurveyParser;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class SurveyActivityPresenter {
    SurveyActivityCommunicationInterface communicationInterface;
    Parser parser;

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
                        communicationInterface.parsedSurveyData(survey);
                    } else {
                        communicationInterface.onError(Constants.ErrorCodes.NULL_DATA);
                    }
                }
            }
        });
        parser.execute(json);
    }

    public void cancelParsingAsyncTask() {
        if (parser != null)
            parser.cancel(true);
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
