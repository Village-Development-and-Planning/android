package org.ptracking.vdp.modals;

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

    public Answer() {
    }

    public static Answer createDummyAnswer(Question parentQuestion) {
        Answer answer = new Answer(new ArrayList<Option>(), parentQuestion);
        answer.setDummy(true);
        return answer;
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
            ArrayList<Question> children = parentQuestion.getChildren();
            if (children != null) {
                for (Question c : children) {
                    Question cCopy = new Question(
                            c.getPosition(),
                            c.getText(),
                            c.getType(),
                            c.getOptions(),
                            c.getTags(),
                            c.getNumber(),
                            c.getChildren(),
                            c.getFlowPattern(),
                            parentQuestion
                    );
                    cCopy.setParentAnswer(this);
                    this.children.add(cCopy);
                }
            }
        }

        this.startTimeStamp = System.currentTimeMillis();
    }

    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy(boolean dummy) {
        isDummy = dummy;
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

    public ArrayList<Option> getLoggedOptions() {
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
        for (Option option : getLoggedOptions()) {
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

//    private void writeObject(ObjectOutputStream os) throws IOException {
//        Timber.i("Writing answer for question number " + parentQuestion.getNumber() + " : " + parentQuestion.hashCode());
//        Timber.i("Answer hashcode " + hashCode());
//        os.writeObject(loggedOptions);
//        os.writeObject(children);
//        os.writeObject(parentQuestion);
//        os.writeLong(startTimeStamp);
//        os.writeLong(exitTimestamp);
//        os.writeBoolean(isDummy);
//        Timber.i("Over Answer ---" + parentQuestion.getNumber() + " : " + parentQuestion.hashCode());
//    }
//
//    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
//        loggedOptions = (ArrayList<Option>) is.readObject();
//        children = (ArrayList<Question>) is.readObject();
//        parentQuestion = (Question) is.readObject();
//        startTimeStamp = is.readLong();
//        exitTimestamp = is.readLong();
//        isDummy = is.readBoolean();
//    }
}
