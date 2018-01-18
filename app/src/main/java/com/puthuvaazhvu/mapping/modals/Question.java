package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;
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

import static com.puthuvaazhvu.mapping.modals.Answer.isAnswerDummy;

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
    private Answer parentAnswer;
    private Question parent;
    private boolean isFinished = false; // you can set to true for the question to skip

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

    private Question(JsonObject json) {
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
        try {
            lock.lock();
            this.optionList = optionList;
        } finally {
            lock.unlock();
        }
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

    public String getTextForLanguage() {
        Text text = getText();
        if (Constants.APP_LANGUAGE == Constants.Language.ENGLISH) return text.getEnglish();
        else return text.getTamil();
    }

    public String getType() {
        return type;
    }

    public ArrayList<Option> getOptionList() {
        return optionList;
    }

    public void setAnswer(Answer answer) {
        try {
            lock.lock();
            setAnswerInternal(answer);
        } finally {
            lock.unlock();
        }
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

    public Answer getLatestAnswer() {
        if (answers.isEmpty()) return null;
        return answers.get(answers.size() - 1);
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

    public static Question findInTreeInternal(QuestionTreeSearchPredicate predicate, Question question) {

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

    public int getIndexOfChild(Question child) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getRawNumber() == child.getRawNumber()) return i;
        }
        return -1;
    }

    public Question findQuestionUpwards(String rawNumber, boolean traverseWithAnswers) {
        Question node = findNodeTraversingBack(this, rawNumber, traverseWithAnswers);
        if (!node.isRoot() && node.getRawNumber().equals(rawNumber)) {
            return node;
        } else {
            return findNodeTraversingForward(node, rawNumber, traverseWithAnswers);
        }
    }

    private Question findNodeTraversingBack(Question node, String rawNumber, boolean traverseWithAnswers) {
        if (node.isRoot()) {
            return node;
        }
        String currentNodeRawNumberWithDot = node.getRawNumber() + ".";
        String currentNodeRawNumber = node.getRawNumber();

        if (rawNumber.equals(currentNodeRawNumber) || rawNumber.contains(currentNodeRawNumberWithDot)) {
            return node;
        } else {
            // move to the parent
            Question parent;
            if (traverseWithAnswers) {
                parent = node.getParentAnswer().getQuestionReference();
            } else {
                parent = node.getParent();
            }
            return findNodeTraversingBack(parent, rawNumber, traverseWithAnswers);
        }
    }

    private Question findNodeTraversingForward(Question node, String rawNumber, boolean traverseWithAnswers) {
        String currentNodeRawNumberWithDot;
        String currentNodeRawNumber;

        if (node.isRoot()) {
            currentNodeRawNumber = "";
            currentNodeRawNumberWithDot = "";
        } else {
            currentNodeRawNumberWithDot = node.getRawNumber() + ".";
            currentNodeRawNumber = node.getRawNumber();
        }

        if (rawNumber.equals(currentNodeRawNumber)) {
            return node;
        } else {
            Question result = null;
            if (node.isRoot() || rawNumber.contains(currentNodeRawNumberWithDot)) {
                // move to child
                List<Question> children;

                if (!traverseWithAnswers) {
                    children = node.getChildren();
                } else {
                    if (node.getAnswers().isEmpty()) {
                        return null;
                    }
                    children = node.getLatestAnswer().getChildren();
                }

                for (Question c : children) {
                    result = findNodeTraversingForward(c, rawNumber, traverseWithAnswers);
                    if (result != null) {
                        break;
                    }
                }
            }
            return result;
        }
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
        if (this.getFlowPattern() == null || answer.getOptions() == null) {
            this.addAnswer(answer);
            return;
        }

        ArrayList<Answer> answersLogged = this.getAnswers();

        AnswerFlow answerFlow = this.getFlowPattern().getAnswerFlow();

        if (answerFlow == null) {
            this.addAnswer(answer);
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
            Answer matchedAnswer = getAnswerMatchForOption(loggedOption.getPosition());

            if (matchedAnswer == null) {
                this.addAnswer(answer);
            } else {
                // update the old answer with the new one
                this.setAnswer(0, answer);
                // latestAnswer = matchedAnswer;
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
    }

    /**
     * returns the reference of the answer that matched the optionID given.
     * returns null if no answer is found
     *
     * @param optionPosition The option position to search for
     * @return already present Answer object else null.
     */
    public Answer getAnswerMatchForOption(String optionPosition) {

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

    public String getQuestionRawNumberPrefix() {
        if (isRoot()) {
            return null;
        }

        int index = rawNumber.lastIndexOf(".");

        if (index >= 0) {
            return rawNumber.substring(0, index);
        } else {
            // single digit
            return rawNumber;
        }
    }

    public static Question moveToQuestion(String snapshotPath, Question root) {
        boolean exception = false;

        String[] indexesInString = snapshotPath.split(",");
        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < indexesInString.length; i++) {
            try {
                indexes.add(Integer.parseInt(indexesInString[i]));
            } catch (NumberFormatException e) {
                Timber.e("Error occurred while parsing snapshot path: " + snapshotPath + " error:" + e.getMessage());
            }
        }

        Question question = root;
        Answer answer = null;

        // we don't want to consider ROOT index and last answer index(if present)
        // make sure we always end with a question.
        for (int i = 1; i < (indexes.size() / 2 == 0 ? indexes.size() - 1 : indexes.size()); i++) {

            int index = indexes.get(i);

            if (i % 2 == 0 && answer != null) {
                // children
                question = answer.getChildren().get(index);
            } else {
                // answer
                answer = question.getAnswers().get(index);
            }

            if (answer == null) {
                exception = true;
                break;
            }
        }

        if (exception) {
            return null;
        } else {
            return question;
        }
    }

    public ArrayList<Integer> getPathOfCurrentQuestion() {
        ArrayList<Integer> indexes = new ArrayList<>();
        getPathOfCurrentQuestion(this, indexes);
        Collections.reverse(indexes);
        return indexes;
    }

    /**
     * Starting index of the path is always Root.
     * Ending index of the path is always Answer.
     *
     * @param node    The current node question to start with
     * @param indexes The list of indexes that contains the path
     */
    public static void getPathOfCurrentQuestion(Question node, ArrayList<Integer> indexes) {
        Question current = node;

        if (current == null) {
            return; // Reached the head of the tree
        }

        if (!current.getAnswers().isEmpty()) {
            int answerCount = current.getAnswers().size();
            Answer lastAnswer = current.getLatestAnswer();

            int answersIndex = -1;

            // traverse through the answers list and find the appropriate index
            for (int i = 0; i < answerCount; i++) {
                if (current.getAnswers().get(i) == lastAnswer) {
                    answersIndex = i;
                    break;
                }
            }

            // add the answer's index
            indexes.add(answersIndex);
        }

        // then add the question's position
        int questionIndex = -1;

        // find the index of this question in it's parent
        Question parent = current.getParent();
        if (parent == null) {
            // ROOT question
            questionIndex = 0;
        } else {

            // find the child's index
            for (int i = 0; i < parent.getChildren().size(); i++) {
                if (parent.getChildren().get(i).getRawNumber().equals(current.getRawNumber())) {
                    questionIndex = i;
                    break;
                }
            }

        }

        indexes.add(questionIndex);

        getPathOfCurrentQuestion(parent, indexes);
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
        return getAsJsonInternal(this);
    }

    private JsonObject getAsJsonInternal(Question node) {
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

        try {

            lock.lock();

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

        } finally {
            lock.unlock();
        }

        // children
        JsonArray childrenArray = new JsonArray();

        if (node.getChildren() != null)
            for (Question c : node.getChildren()) {
                childrenArray.add(getAsJsonInternal(c));
            }

        questionJson.add("children", childrenArray);

        return jsonObject;
    }

    public void removeAnswer() {
        AnswerFlow answerFlow = getFlowPattern().getAnswerFlow();

        if (answerFlow == null) {
            return;
        }

        if (answerFlow.getMode() == AnswerFlow.Modes.ONCE) {
            getAnswers().clear();
        }
        // for OPTION the answers are going ot be updated anyway
        // we don't care for MULTIPLE
    }

    public static ArrayList<Option> noDataWithValidOptions() {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("", "NO DATA",
                new Text("", "", "", ""),
                "", ""));
        return options;
    }

    public static ArrayList<Option> dummyOptions() {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("DUMMY", "DUMMY",
                new Text("DUMMY", "dummy", "dummy", ""),
                "", ""));
        return options;
    }

    public static boolean isLatestAnswerDummy(Question question) {
        if (question.getAnswers().size() <= 0) {
            return false;
        }
        return isAnswerDummy(question.getLatestAnswer());
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
