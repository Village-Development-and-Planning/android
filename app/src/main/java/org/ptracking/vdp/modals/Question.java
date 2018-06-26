package org.ptracking.vdp.modals;

import org.ptracking.vdp.other.Constants;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Question extends BaseObject {
    private String position;
    private Text text;
    private String type;
    private ArrayList<Option> options;
    private ArrayList<String> tags;
    private String number;
    private ArrayList<Question> children;
    private FlowPattern flowPattern;
    private Question parent;

    private Answer parentAnswer;
    private ArrayList<Answer> answers;
    private Answer currentAnswer;
    private boolean isFinished = false; // you can set to true for the question to skip
    private int bubbleAnswersCount;

    public Question() {
        this.answers = new ArrayList<>();
        this.children = new ArrayList<>();
        this.flowPattern = new FlowPattern();
        this.position = "";
        this.number = "";
    }

    public Question(
            String position,
            Text text,
            String type,
            ArrayList<Option> options,
            ArrayList<String> tags,
            String number,
            ArrayList<Question> children,
            FlowPattern flowPattern,
            Question parent
    ) {
        this();
        this.position = position;
        this.text = text;
        this.type = type;
        this.options = options;
        this.tags = tags;
        this.number = number;
        if (children != null)
            this.children = children;
        if (flowPattern != null)
            this.flowPattern = flowPattern;

        this.parent = parent;
    }

    public void setParent(Question parent) {
        this.parent = parent;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setChildren(ArrayList<Question> children) {
        this.children = children;
    }

    public void setFlowPattern(FlowPattern flowPattern) {
        this.flowPattern = flowPattern;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public Question getParent() {
        return parent;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
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

//    private void writeObject(ObjectOutputStream os) throws IOException {
//        Timber.i("Writing question number " + number + " : " + hashCode());
//        os.writeUTF(position);
//        os.writeObject(text);
//        os.writeUTF(type);
//        os.writeObject(options);
//        os.writeObject(tags);
//        os.writeUTF(number);
//        os.writeObject(children);
//        os.writeObject(parent);
//        os.writeObject(flowPattern);
//        os.writeObject(answers);
//        os.writeObject(parentAnswer);
//        os.writeObject(currentAnswer);
//        os.writeBoolean(isFinished);
//        os.writeInt(bubbleAnswersCount);
//        Timber.i("Over Question ---" + number + " : " + hashCode());
//    }
//
//    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
//        position = is.readUTF();
//        text = (Text) is.readObject();
//        type = is.readUTF();
//        options = (ArrayList<Option>) is.readObject();
//        tags = (ArrayList<String>) is.readObject();
//        number = is.readUTF();
//        children = (ArrayList<Question>) is.readObject();
//        parent = (Question) is.readObject();
//        flowPattern = (FlowPattern) is.readObject();
//        answers = (ArrayList<Answer>) is.readObject();
//        parentAnswer = (Answer) is.readObject();
//        currentAnswer = (Answer) is.readObject();
//        isFinished = is.readBoolean();
//        bubbleAnswersCount = is.readInt();
//    }
}
