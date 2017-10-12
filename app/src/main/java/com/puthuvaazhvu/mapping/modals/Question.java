package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.modals.Flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.Flow.FlowPattern;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Question implements Parcelable {
    private final String id;
    private final String position;
    private final Text text;
    private final String type;
    private final ArrayList<Option> optionList;
    private final ArrayList<Answer> answer;
    private final ArrayList<Tag> tag;
    private final String modifiedAt;
    private final String rawNumber;
    private final ArrayList<Question> children;
    private final Info info;
    private final FlowPattern flowPattern;
    private Question parent;

    public Question(String id, String position, Text text, String type, ArrayList<Option> optionList, ArrayList<Answer> answer, ArrayList<Tag> tag, String modifiedAt, String rawNumber, ArrayList<Question> children, Info info, FlowPattern flowPattern, Question parent) {
        this.id = id;
        this.position = position;
        this.text = text;
        this.type = type;
        this.optionList = optionList;
        this.answer = answer;
        this.tag = tag;
        this.modifiedAt = modifiedAt;
        this.rawNumber = rawNumber;
        this.children = children;
        this.info = info;
        this.flowPattern = flowPattern;
        this.parent = parent;
    }

    public Question(JsonObject json) {
        this.position = JsonHelper.getString(json, "position");

        JsonObject questionJson = JsonHelper.getJsonObject(json, "question");
        this.id = JsonHelper.getString(questionJson, "_id");
        this.type = JsonHelper.getString(questionJson, "type");
        this.modifiedAt = JsonHelper.getString(questionJson, "modifiedAt");
        this.rawNumber = JsonHelper.getString(questionJson, "number");

        JsonObject flowJson = JsonHelper.getJsonObject(questionJson, "flow");
        if (flowJson != null)
            this.flowPattern = new FlowPattern(flowJson);
        else this.flowPattern = null;

        JsonObject infoJson = JsonHelper.getJsonObject(questionJson, "info");
        if (infoJson != null)
            this.info = new Info(infoJson);
        else this.info = null;

        JsonArray tagsArray = JsonHelper.getJsonArray(questionJson, "tags");
        if (tagsArray != null)
            this.tag = Tag.getTags(tagsArray);
        else this.tag = null;

        JsonObject textJson = JsonHelper.getJsonObject(questionJson, "text");
        if (textJson != null)
            this.text = new Text(textJson);
        else this.text = null;

        JsonArray optionJsonArray = JsonHelper.getJsonArray(questionJson, "options");
        if (optionJsonArray != null)
            this.optionList = Option.getOptions(optionJsonArray);
        else this.optionList = null;

        this.answer = new ArrayList<>();
        this.children = new ArrayList<>();

        this.parent = null;
    }

    public String getTextString() {
        if (text == null) {
            return null;
        }
        switch (Constants.APP_LANGUAGE) {
            case TAMIL:
                return text.getTamil();
            default:
                return text.getEnglish();
        }
    }

    public void setParent(Question parent) {
        parent.children.add(this);
        this.parent = parent;
    }

    public void addChild(Question child) {
        this.children.add(child);
        child.parent = this;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        if (this.children.size() == 0)
            return true;
        else
            return false;
    }

    public String getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public Text getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public ArrayList<Option> getOptionList() {
        return optionList;
    }

    public void addAnswer(Answer answer) {
        this.answer.add(answer);
    }

    public ArrayList<Answer> getAnswer() {
        return answer;
    }

    public ArrayList<Tag> getTag() {
        return tag;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public String getRawNumber() {
        return rawNumber;
    }

    public ArrayList<Question> getChildren() {
        return children;
    }

    public Question getParent() {
        return parent;
    }

    public Info getInfo() {
        return info;
    }

    public FlowPattern getFlowPattern() {
        return flowPattern;
    }

    public boolean isAnswered() {
        boolean isScopeOnce = false;
        AnswerFlow answerFlow = getFlowPattern().getAnswerFlow();
        if (answerFlow != null) {
            isScopeOnce = answerFlow.getMode() == AnswerFlow.Modes.ONCE;
        }
        return this.answer.size() > 0 && isScopeOnce; // automatically skip if answered once (only for scope: once)
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.position);
        dest.writeParcelable(this.text, flags);
        dest.writeString(this.type);
        dest.writeTypedList(this.optionList);
        dest.writeTypedList(this.answer);
        dest.writeTypedList(this.tag);
        dest.writeString(this.modifiedAt);
        dest.writeString(this.rawNumber);
        dest.writeTypedList(this.children);
        dest.writeParcelable(this.info, flags);
        dest.writeParcelable(this.flowPattern, flags);
        dest.writeParcelable(this.parent, flags);
    }

    protected Question(Parcel in) {
        this.id = in.readString();
        this.position = in.readString();
        this.text = in.readParcelable(Text.class.getClassLoader());
        this.type = in.readString();
        this.optionList = in.createTypedArrayList(Option.CREATOR);
        this.answer = in.createTypedArrayList(Answer.CREATOR);
        this.tag = in.createTypedArrayList(Tag.CREATOR);
        this.modifiedAt = in.readString();
        this.rawNumber = in.readString();
        this.children = in.createTypedArrayList(Question.CREATOR);
        this.info = in.readParcelable(Info.class.getClassLoader());
        this.flowPattern = in.readParcelable(FlowPattern.class.getClassLoader());
        this.parent = in.readParcelable(Question.class.getClassLoader());
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public static Question populateQuestion(JsonObject nodeJson) {
        return populateQuestionInternal(null, nodeJson);
    }

    /**
     * Recursively populates the node.
     *
     * @param question The root node question. (Null usually, if it's starting node)
     * @param nodeJson The corresponding JSON
     * @return The populated SingleQuestionFragment object.
     */
    private static Question populateQuestionInternal(Question question, JsonObject nodeJson) {
        if (question == null) // if null, start of the new node.
            question = new Question(nodeJson);

        JsonObject questionJson = JsonHelper.getJsonObject(nodeJson, "question");
        if (questionJson != null) {
            JsonArray childrenArray = JsonHelper.getJsonArray(questionJson, "children");
            if (childrenArray != null) {
                for (JsonElement e : childrenArray) {
                    JsonObject o = e.getAsJsonObject();
                    Question c = new Question(o);
                    question.addChild(c);
                    populateQuestionInternal(c, o);
                }
            }
        }
        return question;
    }
}
