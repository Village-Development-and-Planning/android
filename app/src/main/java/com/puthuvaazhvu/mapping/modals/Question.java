package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Question extends BaseObject implements Parcelable {

    private final String id;
    private final String position;
    private final Text text;
    private final String type;
    private ArrayList<Option> optionList;
    private final ArrayList<Answer> answers;
    private final ArrayList<Tag> tag;
    private final String modifiedAt;
    private final String rawNumber;
    private final ArrayList<Question> children;
    private final Info info;
    private final FlowPattern flowPattern;
    private Answer parentAnswer;
    private Answer currentAnswer;
    private Question parent;
    private boolean isFinished = false; // you can set to true for the question to skip
    private int bubbleAnswersCount;

    private final Lock lock = new ReentrantLock();

    public Question(String id, String position, Text text, String type, ArrayList<Option> optionList, ArrayList<Answer> answers, ArrayList<Tag> tag, String modifiedAt, String rawNumber, ArrayList<Question> children, Info info, FlowPattern flowPattern, Question parent) {
        this.id = id;
        this.position = position;
        this.text = text;
        this.type = type;
        this.optionList = optionList;
        this.answers = answers;
        this.tag = tag;
        this.modifiedAt = modifiedAt;
        this.rawNumber = rawNumber;
        this.children = children;
        this.info = info;
        this.flowPattern = flowPattern;
        this.parent = parent;
    }

    public Question(Question other) {
        id = other.getId();
        position = other.getPosition();
        text = other.getText();
        type = other.getType();
        optionList = other.getOptionList();
        answers = other.getAnswers();
        tag = other.getTag();
        modifiedAt = other.getModifiedAt();
        rawNumber = other.getRawNumber();
        children = other.getChildren();
        info = other.getInfo();
        flowPattern = other.getFlowPattern();
        parent = other.getParent();
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

        this.tag = null; // not considering tag

        JsonObject textJson = JsonHelper.getJsonObject(questionJson, "text");
        if (textJson != null)
            this.text = new Text(textJson);
        else this.text = null;

        JsonArray optionJsonArray = JsonHelper.getJsonArray(questionJson, "options");
        if (optionJsonArray != null)
            this.optionList = Option.getOptions(optionJsonArray);
        else this.optionList = null;

        this.answers = new ArrayList<>();
        this.children = new ArrayList<>();

        this.parent = null;
    }

    public void setOptionList(ArrayList<Option> optionList) {
        try {
            lock.lock();
            this.optionList = optionList;
        } finally {
            lock.unlock();
        }
    }

    public int getBubbleAnswersCount() {
        return bubbleAnswersCount;
    }

    public void setBubbleAnswersCount(int bubbleAnswersCount) {
        this.bubbleAnswersCount = bubbleAnswersCount;
    }

    public Answer getCurrentAnswer() {
        return currentAnswer;
    }

    public void setCurrentAnswer(Answer currentAnswer) {
        this.currentAnswer = currentAnswer;
    }

    public ArrayList<Option> getOptionList() {
        return optionList;
    }

    public Answer getParentAnswer() {
        return parentAnswer;
    }

    public void setParentAnswer(Answer parentAnswer) {
        this.parentAnswer = parentAnswer;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void setParent(Question parent) {
        parent.children.add(this);
        this.parent = parent;
    }

    public Question getParent() {
        return parent;
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

    private String getId() {
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

    public ArrayList<Answer> getAnswers() {
        return answers;
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

    public void addChild(Question child) {
        this.children.add(child);
        child.parent = this;
    }

    public Info getInfo() {
        return info;
    }

    public FlowPattern getFlowPattern() {
        return flowPattern;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
        this.setCurrentAnswer(answer);
    }

    public void setAnswerAt(int index, Answer answer) {
        this.answers.set(index, answer);
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
        dest.writeTypedList(this.answers);
        dest.writeTypedList(this.tag);
        dest.writeString(this.modifiedAt);
        dest.writeString(this.rawNumber);
        dest.writeTypedList(this.children);
        dest.writeParcelable(this.info, flags);
        dest.writeParcelable(this.flowPattern, flags);
        dest.writeParcelable(this.parent, flags);
        dest.writeParcelable(this.parentAnswer, flags);
    }

    protected Question(Parcel in) {
        this.id = in.readString();
        this.position = in.readString();
        this.text = in.readParcelable(Text.class.getClassLoader());
        this.type = in.readString();
        this.optionList = in.createTypedArrayList(Option.CREATOR);

        this.answers = in.createTypedArrayList(Answer.CREATOR);
        // avoid circular reference error.
        for (Answer answer : answers) {
            answer.setQuestionReference(this);
        }

        this.tag = in.createTypedArrayList(com.puthuvaazhvu.mapping.modals.Tag.CREATOR);
        this.modifiedAt = in.readString();
        this.rawNumber = in.readString();
        this.children = in.createTypedArrayList(Question.CREATOR);
        this.info = in.readParcelable(Info.class.getClassLoader());
        this.flowPattern = in.readParcelable(FlowPattern.class.getClassLoader());
        this.parent = in.readParcelable(Question.class.getClassLoader());
        this.parentAnswer = in.readParcelable(Answer.class.getClassLoader());
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

    @Override
    public JsonElement getAsJson() {
        return QuestionUtils.convertToJson(this);
    }

    @Override
    public Question copy() {

        ArrayList<Question> childrenCopy = new ArrayList<>();

        if (children != null) {

            // copy only the first siblings.
            for (Question c : children) {

                // copy answers as well to avoid duplicate entries.
                ArrayList<Answer> answersCopy = new ArrayList<>();
                for (Answer a : c.getAnswers()) {
                    answersCopy.add(a.copy());
                }

                childrenCopy.add(
                        new Question(
                                c.id,
                                c.position,
                                c.text,
                                c.type,
                                c.optionList,
                                answersCopy,
                                c.tag,
                                c.modifiedAt,
                                c.rawNumber,
                                c.children,
                                c.info,
                                c.flowPattern,
                                this
                        )
                );
            }

        }

        return new Question(
                id,
                position,
                text,
                type,
                optionList,
                answers,
                tag,
                modifiedAt,
                rawNumber,
                childrenCopy,
                info,
                flowPattern,
                getParent()
        );
    }

    @Override
    public String toString() {
        return "Raw Number " + rawNumber + "\n" +
                "Children count " + (children != null ? children.size() : 0) + "\n" +
                "Answers count " + (answers != null ? answers.size() : 0) + "\n" +
                "Parent " + (parent == null ? "ROOT" : parent.getRawNumber()) + "\n";
    }
}
