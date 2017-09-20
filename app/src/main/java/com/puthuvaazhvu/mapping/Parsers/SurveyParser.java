package com.puthuvaazhvu.mapping.Parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.Modals.Option;
import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.Modals.Text;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class SurveyParser {
    private static SurveyParser surveyParser;
    String json;

    public static SurveyParser getInstance() {
        if (surveyParser == null) {
            surveyParser = new SurveyParser();
        }
        return surveyParser;
    }

    private SurveyParser() {
    }

    public Survey parseSurvey(JsonObject json) {
        String id = JsonHelper.getString(json, "_id");
        String name = JsonHelper.getString(json, "name");
        String modifiedAt = JsonHelper.getString(json, "modifiedAt");

        JsonArray questionsArray = JsonHelper.getJsonArray(json, "questions");
        ArrayList<Question> questionList = new ArrayList<>();

        if (questionsArray != null) {
            for (JsonElement e : questionsArray) {
                questionList.add(parseQuestion(e.getAsJsonObject()));
            }
        }

        return new Survey(id, name, questionList, modifiedAt);
    }

    public Question parseQuestion(JsonObject json) {
        String position = JsonHelper.getString(json, "position");
        JsonObject questionJson = JsonHelper.getJsonObject(json, "question");
        String id = JsonHelper.getString(questionJson, "_id");
        String type = JsonHelper.getString(questionJson, "type");
        String modifiedAt = JsonHelper.getString(questionJson, "modifiedAt");
        String rawNumber = JsonHelper.getString(questionJson, "number");
        Question.Info info = parseQuestionInfo(questionJson);

        JsonObject textJson = JsonHelper.getJsonObject(questionJson, "text");

        Text text = null;

        if (textJson != null) {
            text = parseText(textJson);
        }

        JsonArray optionsArray = JsonHelper.getJsonArray(questionJson, "options");
        ArrayList<Option> optionList = new ArrayList<>();

        if (optionsArray != null) {
            for (JsonElement e : optionsArray) {
                Option option = parseOption(e.getAsJsonObject());
                if (option != null) {
                    optionList.add(option);
                }
            }
        }

        ArrayList<String> tags = JsonHelper.getStringArray(questionJson, "tags");

        JsonArray childrenArray = JsonHelper.getJsonArray(questionJson, "children");
        ArrayList<Question> children = new ArrayList<>();

        if (childrenArray != null) {
            for (JsonElement e : childrenArray) {
                children.add(parseQuestion(e.getAsJsonObject()));
            }
        }

        return new Question(id, position, rawNumber, text, type, optionList, tags, modifiedAt, children, info);
    }

    public Option parseOption(JsonObject json) {
        String position = JsonHelper.getString(json, "position");

        JsonObject optionJson = JsonHelper.getJsonObject(json, "option");

        String id = "";
        String type = "";

        if (optionJson != null) {
            id = JsonHelper.getString(optionJson, "_id");
            type = JsonHelper.getString(optionJson, "type");
        }

        JsonObject textJson = JsonHelper.getJsonObject(optionJson, "text");

        Text text = null;

        if (textJson != null) {
            text = parseText(textJson);
        }

        String modifiedAT = JsonHelper.getString(optionJson, "modifiedAt");

        Option option = null;

        if (!id.isEmpty() && text != null) {
            option = new Option(id, type, text, modifiedAT, position);
        }

        return option;
    }

    public Text parseText(JsonObject json) {
        String english = JsonHelper.getString(json, "english");
        String tamil = JsonHelper.getString(json, "tamil");
        String id = JsonHelper.getString(json, "_id");
        return new Text(id, english, tamil);
    }

    public Question.Info parseQuestionInfo(JsonObject jsonObject) {
        Question.Info info = null;
        JsonObject infoJson = JsonHelper.getJsonObject(jsonObject, "info");
        if (infoJson != null) {
            String questionNumber = JsonHelper.getString(infoJson, "question");
            String option = JsonHelper.getString(infoJson, "option");
            if (!questionNumber.isEmpty() && !option.isEmpty())
                info = new Question.Info(questionNumber, option);
        }
        return info;
    }
}
