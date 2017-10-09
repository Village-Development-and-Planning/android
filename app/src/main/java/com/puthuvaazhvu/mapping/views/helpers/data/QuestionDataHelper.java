package com.puthuvaazhvu.mapping.views.helpers.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.Types;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/9/17.
 */

public class QuestionDataHelper {
    private final Question root;

    public QuestionDataHelper(Question root) {
        this.root = root;
    }

    public Question find(String id) {
        return getQuestion(id, root);
    }

    /**
     * Helper to get the question from the given question root. Recursive
     *
     * @param id The ID question to be found
     * @return The found question or null if not found
     */
    private Question getQuestion(String id, Question question) {
        Question result = null;
        if (question.getId().equals(id)) {
            return question;
        }
        for (Question q : question.getChildren()) {
            result = getQuestion(id, q);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public static class OtherHelpers {
        /**
         * Helper to save the options into the selected reference question.
         *
         * @param data     The data from UI (includes the options data as well)
         * @param question The reference question
         * @return The updated reference question in it's answer field.
         */
        public static Question updateQuestion(Data data, Question question) {
            com.puthuvaazhvu.mapping.views.fragments.option.modals.Data responseData = data.getResponseData();

            if (responseData == null) {
                throw new IllegalArgumentException("The response data for the given question id "
                        + data.getQuestion().getId() + " is null.");
            }

            com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.Answer responseAnswer = responseData.getAnswer();
            String optionID = responseAnswer.getOptionID();
            String optionType = responseAnswer.getOptionType();
            JsonElement dataElement = responseAnswer.getOptionData();

            ArrayList<Option> loggedOption = new ArrayList<>();

            if (optionID != null) {
                Option option = getOption(question, optionID);
                if (option != null) {
                    loggedOption.add(option);
                } else {
                    throw new IllegalArgumentException("The option with ID is not found. " + optionID);
                }
            } else {
                Option option;
                JsonObject jsonObject;
                switch (optionType) {
                    case Types.GPS:
                        jsonObject = dataElement.getAsJsonObject();
                        double lat = jsonObject.get("lat").getAsDouble();
                        double lng = jsonObject.get("lng").getAsDouble();
                        String gps = lat + "," + lng;
                        // add a new option with no id
                        loggedOption.add(new Option(null
                                , optionType
                                , new Text(null, gps, gps, null)
                                , null
                                , null));
                        break;
                    case Types.INPUT:
                        jsonObject = dataElement.getAsJsonObject();
                        String text = jsonObject.get("text").getAsString();
                        // add a new option with no id
                        loggedOption.add(new Option(null
                                , optionType
                                , new Text(null, text, text, null)
                                , null
                                , null));
                        break;
                    case Types.MULTIPLE:
                        JsonArray jsonArray = dataElement.getAsJsonArray();
                        for (JsonElement e : jsonArray) {
                            String oID = e.getAsJsonObject().get("id").getAsString();
                            option = getOption(question, oID);
                            if (option != null) {
                                loggedOption.add(option);
                            } else {
                                throw new IllegalArgumentException("The option with ID is not found. " + oID);
                            }
                        }
                        break;
                    case Types.SINGLE:
                        option = getOption(question, optionID);
                        if (option != null) {
                            loggedOption.add(option);
                        } else {
                            throw new IllegalArgumentException("The option with ID is not found. " + optionID);
                        }
                        break;
                }
            }

            Answer answer = new Answer(loggedOption, question);
            question.addAnswer(answer);
            return question;
        }

        private static Option getOption(Question question, String optionID) {
            ArrayList<Option> options = question.getOptionList();
            for (Option o : options) {
                if (o.getId().equals(optionID)) {
                    return o;
                }
            }
            return null;
        }

    }

    public static class Adapters {
        /**
         * Helper to convert the {@link Question} children to {@link GridData}
         *
         * @param question The question to be converted
         * @return grid data modal.
         */
        public static ArrayList<GridData> getDataForGrid(Question question) {
            ArrayList<Question> children = question.getChildren();
            ArrayList<GridData> result = new ArrayList<>(children.size());
            for (Question c : children) {
                result.add(GridData.adapter(c));
            }
            return result;
        }
    }
}
