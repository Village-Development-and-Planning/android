package com.puthuvaazhvu.mapping.modals.flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.BaseObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/26/17.
 */

public class FlowPattern extends BaseObject implements Parcelable {
    private final PreFlow preFlow;
    private final QuestionFlow questionFlow;
    private final AnswerFlow answerFlow;
    private final ChildFlow childFlow;
    private final ArrayList<String> postFlow;
    private final ExitFlow exitFlow;
    private JsonObject jsonObject;

    public FlowPattern(JsonObject jsonObject) {
        this.jsonObject = jsonObject;

        JsonObject preJson = JsonHelper.getJsonObject(jsonObject, "pre");
        JsonArray postJson = JsonHelper.getJsonArray(jsonObject, "post");
        JsonObject questionJson = JsonHelper.getJsonObject(jsonObject, "question");
        JsonObject answerJson = JsonHelper.getJsonObject(jsonObject, "answer");
        JsonObject childJson = JsonHelper.getJsonObject(jsonObject, "child");
        JsonArray postJsonArray = JsonHelper.getJsonArray(jsonObject, "post");
        JsonObject exitJson = JsonHelper.getJsonObject(jsonObject, "exit");

        if (preJson != null)
            this.preFlow = new PreFlow(preJson);
        else this.preFlow = null;

//        if (postJson != null)
//            this.postFlow = new PostFlow(preJson);
//        else this.postFlow = null;

        if (postJson != null)
            postFlow = (JsonHelper.getStringArray(postJson));
        else postFlow = null;

        if (questionJson != null)
            this.questionFlow = new QuestionFlow(questionJson);
        else this.questionFlow = null;

        if (answerJson != null)
            this.answerFlow = new AnswerFlow(answerJson);
        else this.answerFlow = null;

        if (childJson != null)
            this.childFlow = new ChildFlow(childJson);
        else this.childFlow = null;

        if (exitJson != null)
            this.exitFlow = new ExitFlow(exitJson);
        else this.exitFlow = null;
    }

    public PreFlow getPreFlow() {
        return preFlow;
    }

    public QuestionFlow getQuestionFlow() {
        return questionFlow;
    }

    public AnswerFlow getAnswerFlow() {
        return answerFlow;
    }

    public ChildFlow getChildFlow() {
        return childFlow;
    }

    public ArrayList<String> getPostFlow() {
        return postFlow;
    }

    public ExitFlow getExitFlow() {
        return exitFlow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.preFlow, flags);
        dest.writeParcelable(this.questionFlow, flags);
        dest.writeParcelable(this.answerFlow, flags);
        dest.writeParcelable(this.childFlow, flags);
        dest.writeStringList(this.postFlow);
        dest.writeParcelable(this.exitFlow, flags);
    }

    protected FlowPattern(Parcel in) {
        this.preFlow = in.readParcelable(PreFlow.class.getClassLoader());
        this.questionFlow = in.readParcelable(QuestionFlow.class.getClassLoader());
        this.answerFlow = in.readParcelable(AnswerFlow.class.getClassLoader());
        this.childFlow = in.readParcelable(ChildFlow.class.getClassLoader());
        this.postFlow = in.createStringArrayList();
        this.exitFlow = in.readParcelable(ExitFlow.class.getClassLoader());
    }

    public static final Creator<FlowPattern> CREATOR = new Creator<FlowPattern>() {
        @Override
        public FlowPattern createFromParcel(Parcel source) {
            return new FlowPattern(source);
        }

        @Override
        public FlowPattern[] newArray(int size) {
            return new FlowPattern[size];
        }
    };

    @Override
    public JsonElement getAsJson() {
        return jsonObject;
    }
}
