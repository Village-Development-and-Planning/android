package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.flow.AnswerFlow;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Question extends BaseObject implements Parcelable {
    private final String id;
    private final String position;
    private final Text text;
    private final String type;
    private final ArrayList<Option> optionList;
    private final ArrayList<Answer> answers;
    private final ArrayList<Tag> tag;
    private final String modifiedAt;
    private final String rawNumber;
    private final ArrayList<Question> children;
    private final Info info;
    private final FlowPattern flowPattern;

    private Answer currentAnswer;
    private Question parent;
    private boolean isFinished = false; // you can set to true for the question to skip


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

    // shallow copy
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
//        JsonArray tagsArray = JsonHelper.getJsonArray(questionJson, "tags");
//        if (tagsArray != null)
//            this.tag = Tag.getTags(tagsArray);
//        else this.tag = null;

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

    public void replaceParent(Question parent) {
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

    public void setAnswer(Answer answer) {
        setAnswerInternal(answer);
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

    public Question getParent() {
        return parent;
    }

    public Info getInfo() {
        return info;
    }

    public FlowPattern getFlowPattern() {
        return flowPattern;
    }

    private void setCurrentAnswer(Answer answer) {
        currentAnswer = answer;
    }

    public Answer getCurrentAnswer() {
        return currentAnswer;
    }

    private void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    private void setAnswer(int index, Answer answer) {
        this.answers.set(index, answer);
    }

//    public Answer getLatestAnswer() {
//        if (answers == null || answers.isEmpty()) {
//            return null;
//        }
//        return answers.get(answers.size() - 1); // The required answers is always at the last index.
//    }

    public static Question populateQuestion(JsonObject nodeJson) {
        return populateQuestionInternal(null, nodeJson);
    }

    private void setAnswerInternal(Answer answer) {
        ArrayList<Answer> answersLogged = this.getAnswers();
        AnswerFlow answerFlow = this.getFlowPattern().getAnswerFlow();

        if (answerFlow == null) {
            this.addAnswer(answer);
            setCurrentAnswer(answer);
            return;
        }

        Answer currentAnswer = answer;

        if (answerFlow.getMode() == AnswerFlow.Modes.OPTION) {

            // assuming only one answer in option types
            if (answer.getOptions().size() > 1) {
                throw new IllegalArgumentException("Currently we assume only 1 answer size for options scope.");
            }

            if (answer.getOptions().isEmpty()) {
                throw new IllegalArgumentException("The options are empty.");
            }

            Option loggedOption = answer.getOptions().get(0);
            Answer matchedAnswer = getAnswerMatch(loggedOption.getId());

            if (matchedAnswer == null) {
                this.addAnswer(answer);
            } else {
                currentAnswer = matchedAnswer;
            }

        } else if (answerFlow.getMode() == AnswerFlow.Modes.ONCE) {

            if (answersLogged.size() > 0) {
                // already answered so update
                this.setAnswer(0, answer);
            } else {
                this.addAnswer(answer);
            }

        } else {
            this.addAnswer(answer);
        }

        setCurrentAnswer(currentAnswer);

    }

    /**
     * returns the reference of the answer that matched the optionID given.
     * returns null if no answer is found
     *
     * @param optionID The option ID to search for
     * @return already present Answer object else null.
     */
    private Answer getAnswerMatch(String optionID) {

        if (flowPattern.getAnswerFlow().getMode() == AnswerFlow.Modes.OPTION) {

            ArrayList<Answer> answersLogged = this.getAnswers();

            if (answersLogged != null) {

                for (Answer answer : answersLogged) {

                    ArrayList<Option> options = answer.getOptions();

                    for (Option option : options) {
                        if (option.getId().equals(optionID)) {
                            return answer;
                        }
                    }
                }
            }

        }
        return null;
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
    }

    protected Question(Parcel in) {
        this.id = in.readString();
        this.position = in.readString();
        this.text = in.readParcelable(Text.class.getClassLoader());
        this.type = in.readString();
        this.optionList = in.createTypedArrayList(Option.CREATOR);
        this.answers = in.createTypedArrayList(Answer.CREATOR);
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

    @Override
    public JsonElement getAsJson() {
        return getAsJsonInternal(this);
    }

    private static JsonObject getAsJsonInternal(Question node) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", node.getId());
        jsonObject.addProperty("position", node.getPosition());

        if (node.getText() != null)
            jsonObject.add("text", node.getText().getAsJson());

        jsonObject.addProperty("type", node.getType());
        jsonObject.addProperty("number", node.getRawNumber());

        // options
        JsonArray optionsArray = new JsonArray();

        if (node.getOptionList() != null)
            for (Option o : node.getOptionList()) {
                optionsArray.add(o.getAsJson());
            }

        jsonObject.add("options", optionsArray);

        // answers
        JsonArray answersArray = new JsonArray();

        if (node.getAnswers() != null)
            for (Answer a : node.getAnswers()) {
                answersArray.add(a.getAsJson());
            }

        jsonObject.add("answers", answersArray);

        // children
        JsonArray childrenArray = new JsonArray();

        if (node.getChildren() != null)
            for (Question c : node.getChildren()) {
                childrenArray.add(getAsJsonInternal(c));
            }

        jsonObject.add("children", childrenArray);

        return jsonObject;
    }
}
