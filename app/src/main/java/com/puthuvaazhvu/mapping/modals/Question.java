package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Question extends BaseObject implements Parcelable {

    public interface QuestionTreeSearchPredicate {
        boolean evaluate(Question question);
    }

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

    public void setOptionList(ArrayList<Option> optionList) {
        this.optionList = optionList;
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

    public Question findInTree(QuestionTreeSearchPredicate predicate) {
        return findInTreeInternal(predicate, this);
    }

    private Question findInTreeInternal(QuestionTreeSearchPredicate predicate, Question question) {

        if (predicate.evaluate(question)) {
            return question;
        }

        Question result = null;

        for (Question child : question.getChildren()) {

            result = findInTreeInternal(predicate, child);

            if (result != null) {
                break;
            }

        }

        return result;
    }

    public boolean containsPreFlow(String tag) {
        if (flowPattern == null) {
            return false;
        }

        PreFlow preFlow = flowPattern.getPreFlow();
        if (preFlow != null) {
            ArrayList<String> fillTags = preFlow.getFill();
            if (fillTags != null) {
                for (String s : fillTags) {
                    if (tag.toLowerCase().equals(s.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setAnswerInternal(Answer answer) {
        if (this.getFlowPattern() == null) {
            this.addAnswer(answer);
            setCurrentAnswer(answer);
            return;
        }

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
            Answer matchedAnswer = getAnswerMatch(loggedOption.getPosition());

            if (matchedAnswer == null) {
                this.addAnswer(answer);
            } else {
                // update the old answer with the new one
                this.setAnswer(0, answer);
                // currentAnswer = matchedAnswer;
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
     * @param optionPosition The option position to search for
     * @return already present Answer object else null.
     */
    public Answer getAnswerMatch(String optionPosition) {

        if (flowPattern.getAnswerFlow().getMode() == AnswerFlow.Modes.OPTION) {

            ArrayList<Answer> answersLogged = this.getAnswers();

            if (answersLogged != null) {

                for (Answer answer : answersLogged) {

                    ArrayList<Option> options = answer.getOptions();

                    for (Option option : options) {
                        if (option.getPosition() != null && option.getPosition().equals(optionPosition)) {
                            return answer;
                        }
                    }
                }
            }

        }
        return null;
    }

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

    /**
     * Helper to recursively populate the answers from the given answerJson
     *
     * @param nodeJson
     * @return - Observable with the populated question
     */
    public Single<Question> populateAnswers(final JsonObject nodeJson) {
        return Single.create(new SingleOnSubscribe<Question>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Question> emitter) throws Exception {
                Question question = populateAnswersInternal(Question.this, nodeJson);
                emitter.onSuccess(question);
            }
        });
    }

    public static Question populateAnswersInternal(final Question node, JsonObject nodeJson) {

        if (nodeJson.has("question"))
            nodeJson = JsonHelper.getJsonObject(nodeJson, "question");

        JsonArray answersJsonArray = JsonHelper.getJsonArray(nodeJson, "answers");

        if (answersJsonArray != null) {

            for (int i = 0; i < answersJsonArray.size(); i++) {

                JsonObject answersJson = answersJsonArray.get(i).getAsJsonObject();
                JsonArray loggedOptionsJsonArray = JsonHelper.getJsonArray(answersJson, "logged_options");

                ArrayList<Option> options = new ArrayList<>();

                if (loggedOptionsJsonArray != null) {

                    for (int j = 0; j < loggedOptionsJsonArray.size(); j++) {
                        options.add(new Option(loggedOptionsJsonArray.get(j).getAsJsonObject()));
                    }

                }

                // create the answer object
                Answer answer = new Answer(options, node);
                node.setAnswer(answer);

                // answers children length and this array length should be the same
                JsonArray childrenJsonArray = JsonHelper.getJsonArray(answersJson, "children");

                if (childrenJsonArray != null) {

                    if (childrenJsonArray.size() != answer.getChildren().size()) {
                        throw new IllegalArgumentException("The length should be the same.");
                    }

                    for (int k = 0; k < childrenJsonArray.size(); k++) {

                        Question child = answer.getChildren().get(k);
                        JsonObject childJson = childrenJsonArray.get(k).getAsJsonObject();

                        populateAnswersInternal(child, childJson);

                    }
                }
            }
        }

        return node;
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
        this.tag = in.createTypedArrayList(com.puthuvaazhvu.mapping.modals.Tag.CREATOR);
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
        jsonObject.addProperty("position", node.getPosition());

        JsonObject questionJson = new JsonObject();
        jsonObject.add("question", questionJson);

        questionJson.add("flow", node.getFlowPattern().getAsJson());

        questionJson.addProperty("id", node.getId());
        //jsonObject.addProperty("position", node.getPosition());

        if (node.getText() != null)
            questionJson.add("text", node.getText().getAsJson());

        questionJson.addProperty("type", node.getType());
        questionJson.addProperty("number", node.getRawNumber());

        // options
        JsonArray optionsArray = new JsonArray();

        if (node.getOptionList() != null)
            for (Option o : node.getOptionList()) {
                optionsArray.add(o.getAsJson());
            }

        questionJson.add("options", optionsArray);

        // answers
        JsonArray answersArray = new JsonArray();

        if (node.getAnswers() != null)
            for (Answer a : node.getAnswers()) {
                answersArray.add(a.getAsJson());
            }

        questionJson.add("answers", answersArray);

        // children
        JsonArray childrenArray = new JsonArray();

        if (node.getChildren() != null)
            for (Question c : node.getChildren()) {
                childrenArray.add(getAsJsonInternal(c));
            }

        questionJson.add("children", childrenArray);

        return jsonObject;
    }

    @Override
    public Question copy() {

        ArrayList<Question> childrenCopy = new ArrayList<>();

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
}
