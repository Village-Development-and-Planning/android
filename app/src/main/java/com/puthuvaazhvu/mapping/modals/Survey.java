package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.utils.Optional;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Survey extends BaseObject implements Parcelable {
    private final String id;
    private final String name;
    private final String description;
    private final Question rootQuestion;
    private final String modifiedAt;

    public Survey(String id, String name, String description, Question rootQuestion, String modifiedAt) {
        this.id = id;
        this.name = name;
        this.rootQuestion = rootQuestion;
        this.modifiedAt = modifiedAt;
        this.description = description;
    }

    public Survey(JsonObject json) {
        id = JsonHelper.getString(json, "_id");
        name = JsonHelper.getString(json, "name");
        modifiedAt = JsonHelper.getString(json, "modifiedAt");
        description = JsonHelper.getString(json, "description");

        JsonObject questionsJson = JsonHelper.getJsonObject(json, "question");

        if (questionsJson != null) {
            rootQuestion = Question.populateQuestion(json);
        } else {
            rootQuestion = null;
        }
    }

    public static Single<Survey> getSurveyInstanceWithUpdatedAnswers(final JsonObject surveyJson) {
        return Single.just(new Survey(surveyJson))
                .map(new Function<Survey, Survey>() {
                    @Override
                    public Survey apply(@NonNull Survey survey) throws Exception {
                        JsonObject questionJson = JsonHelper.getJsonObject(surveyJson, "question");
                        if (questionJson != null) {
                            Question.populateAnswersInternal(survey.getRootQuestion(), questionJson);
                        }
                        return survey;
                    }
                });
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Question getRootQuestion() {
        return rootQuestion;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public String getDescription() {
        return description;
    }

    public boolean dynamicOptionsFillForQuestion(final String fillTag, ArrayList<Option> options) {

        Question root = rootQuestion;

        Question result = root.findInTree(new Question.QuestionTreeSearchPredicate() {
            @Override
            public boolean evaluate(Question question) {
                return question.containsPreFlow(fillTag);
            }
        });

        if (result == null) {
            return false;
        }

        result.setOptionList(options);

        return true;
    }

    @Override
    public JsonElement getAsJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", id);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);
        jsonObject.add("question", rootQuestion.getAsJson());

        return jsonObject;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeParcelable(this.rootQuestion, flags);
        dest.writeString(this.modifiedAt);
    }

    protected Survey(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.rootQuestion = in.readParcelable(Question.class.getClassLoader());
        this.modifiedAt = in.readString();
    }

    public static final Creator<Survey> CREATOR = new Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel source) {
            return new Survey(source);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };
}
