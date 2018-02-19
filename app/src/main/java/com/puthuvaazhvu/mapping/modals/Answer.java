package com.puthuvaazhvu.mapping.modals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/26/17.
 */

public class Answer extends BaseObject {
    private ArrayList<Option> loggedOptions;
    private ArrayList<Question> children;
    private Question parentQuestion;
    private long startTimeStamp;
    private long exitTimestamp;
    private boolean isDummy;
    private boolean isDummyButValid;

    public static Answer createDummyAnswer() {
        Answer answer = new Answer();
        answer.setDummy(true);
        return answer;
    }

    public static Answer createDummyValidAnswer() {
        Answer answer = new Answer();
        answer.setDummyButValid(true);
        return answer;
    }

    private Answer() {
    }

    public Answer(ArrayList<Option> options, Question parentQuestion, long startTimeStamp) {
        this(options, parentQuestion);
        this.startTimeStamp = startTimeStamp;
    }

    public Answer(ArrayList<Option> options, Question parentQuestion) {
        this.loggedOptions = options;
        this.parentQuestion = parentQuestion;

        this.children = new ArrayList<>();

        if (parentQuestion != null) {
            // copy all the immediate children
            for (Question c : parentQuestion.getChildren()) {
                Question q = new Question(
                        c.getPosition(),
                        c.getText(),
                        c.getType(),
                        c.getOptions(),
                        c.getTags(),
                        c.getNumber(),
                        c.getChildren(),
                        c.getFlowPattern()
                );
                this.children.add(q);
            }
        }

        // set the parent answer of the children
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setParentAnswer(this);
        }

        this.startTimeStamp = System.currentTimeMillis();
    }

    public boolean isDummyButValid() {
        return isDummyButValid;
    }

    public void setDummyButValid(boolean dummyButValid) {
        isDummyButValid = dummyButValid;
    }

    public void setParentQuestion(Question parentQuestion) {
        this.parentQuestion = parentQuestion;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy(boolean dummy) {
        isDummy = dummy;
    }

    public ArrayList<Option> getLoggedOptions() {
        return loggedOptions;
    }

    public void setLoggedOptions(ArrayList<Option> loggedOptions) {
        this.loggedOptions.clear();
        this.loggedOptions.addAll(loggedOptions);
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public long getExitTimestamp() {
        return exitTimestamp;
    }

    public ArrayList<Option> getOptions() {
        return loggedOptions;
    }

    public void setExitTimestamp(long exitTimestamp) {
        this.exitTimestamp = exitTimestamp;
    }

    public void setStartTimeStamp(long timeStamp) {
        this.startTimeStamp = timeStamp;
    }

    public ArrayList<Question> getChildren() {
        return children;
    }

    public Question getParentQuestion() {
        return parentQuestion;
    }

    public boolean containsOption(String position) {
        for (Option option : getOptions()) {
            if (option.getPosition().equals(position)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String string = "";

        string += "hashcode : " + Integer.toHexString(System.identityHashCode(this));

        string += "\nOptions count: " + (loggedOptions == null ? 0 : loggedOptions.size());
        string += "\nChildren [";

        for (Question c : children) {
            string += c.getNumber();
            string += ", ";
        }

        string += "]";

        string += "\nReference question :" + parentQuestion.getNumber();

        return string;
    }
}
