package com.puthuvaazhvu.mapping.modals;

import com.puthuvaazhvu.mapping.other.Constants;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Question extends BaseObject {
    private final String position;
    private final Text text;
    private final String type;
    private ArrayList<Option> options;
    private final ArrayList<String> tags;
    private final String number;
    private final ArrayList<Question> children;
    private final FlowPattern flowPattern;

    private Answer parentAnswer;
    private final ArrayList<Answer> answers;
    private Answer currentAnswer;
    private Question parent;
    private boolean isFinished = false; // you can set to true for the question to skip
    private int bubbleAnswersCount;

    public Question(
            String position,
            Text text,
            String type,
            ArrayList<Option> options,
            ArrayList<String> tags,
            String number,
            ArrayList<Question> children,
            FlowPattern flowPattern
    ) {
        this.position = position;
        this.text = text;
        this.type = type;
        this.options = options;
        this.tags = tags;
        this.number = number;
        this.children = children;
        this.flowPattern = flowPattern;

        this.answers = new ArrayList<>();

        for (Question c : children) {
            c.setParent(this);
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

    public ArrayList<Option> getOptions() {
        return options;
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

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getNumber() {
        return number;
    }

    public ArrayList<Question> getChildren() {
        return children;
    }

    public FlowPattern getFlowPattern() {
        return flowPattern;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
        setCurrentAnswer(answer);
    }

    public String getTextString() {
        if (getText() == null) {
            return null;
        }
        switch (Constants.APP_LANGUAGE) {
            case TAMIL:
                return getText().getTamil();
            default:
                return getText().getEnglish();
        }
    }

    @Override
    public String toString() {
        return "Raw Number " + number + "\n" +
                "Children count " + (children != null ? children.size() : 0) + "\n" +
                "Answers count " + (answers != null ? answers.size() : 0) + "\n" +
                "Parent " + (parent == null ? "ROOT" : parent.getNumber()) + "\n";
    }
}
